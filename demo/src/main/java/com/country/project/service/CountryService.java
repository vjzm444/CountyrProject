package com.country.project.service;


import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.country.project.config.exception.CustomException;
import com.country.project.config.exception.ErrorCode;
import com.country.project.initializer.DataInitializer;
import com.country.project.model.CountryEntity;
import com.country.project.model.PublicHolidayEntity;
import com.country.project.model.jsonModel.PublicHoliday;
import com.country.project.repository.CountryRepository;
import com.country.project.repository.HolidayRepository;

import jakarta.transaction.Transactional;


/**
 * H2에서 데이터를 가져온다
 *  - 추후 Redis로 변경해서 가져오는것도 고려.(DB부하 저하를 위해)
 */
@Service
public class CountryService {

    //로거
    private static final Logger logger = LoggerFactory.getLogger(CountryService.class);

    private final RestTemplate restTemplate;

    /**
     * HolidayRepository
     */
    @Autowired
    private HolidayRepository holidayRepository;
    /**
     * CountryRepository
     */
    @Autowired
    private CountryRepository countryRepository;
    
    // 생성자
    public CountryService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * 국가코드 조회
     */
    public List<CountryEntity> getAvailableCountries() {
        List<CountryEntity> response = countryRepository.findAll();

        if (response != null && !response.isEmpty()) {
            logger.info("CountryEntity API called, number of items: {}", response.size());
        } else {
            logger.warn("CountryEntity API nodata");
        }

        return response;
    }


    /**
     * 휴일날 조회
     * @param year
     * @param country
     * @return
     */
    public Page<PublicHolidayEntity> getPublicHolidays(String year,Integer month, String country,int page) {
        
        //나라코드 존재확인
        CountryEntity codeSheachResult = countryRepository.findByCountryCode(country);
        if(codeSheachResult == null){
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        
        int pageSize = 5; //페이징수
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("date").ascending());

        Page<PublicHolidayEntity> holidays;

        if (month != null) {
            // 선택한 달의 시작일과 마지막일 계산
            LocalDate startDate = LocalDate.of(Integer.parseInt(year), month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            //logger.info("month: {}, startDate: {}, endDate: {}", month, startDate, endDate);
            
            holidays = holidayRepository.findByCountry_CountryCodeAndDateBetween(country, startDate, endDate, pageable);
        } else {
            // month가 없으면 기존처럼 연도+국가 기준 조회
            holidays = holidayRepository.findByHolidayYearAndCountry_CountryCode(year, country, pageable);
        }

        return holidays;
    }

    /**
     * 해당년도인 국가의 휴일 삭제
     */
    @Transactional
    public int deleteHolidays(String year, String country) {

        //나라코드 존재확인
        CountryEntity codeSheachResult = countryRepository.findByCountryCode(country);
        if(codeSheachResult == null){
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        
        //조회
        List<PublicHolidayEntity> holidays = holidayRepository.findByHolidayYearAndCountry_CountryCode(year, country);
        //삭제 할 요소가 있는지?
        if (holidays.isEmpty()) {
            throw new CustomException(ErrorCode.POSTS_NOT_FOUND);
        }
        
        //삭제
        holidayRepository.deleteAll(holidays);
        logger.info("delete holidays year: {}, country:{}, count: {}", year, country, holidays.size());
        return holidays.size();
    }

    /**
     * 특정 연도·국가 공휴일 데이터 재호출 후 Upsert
     *  
     * @param year 연도
     * @param country 국가 코드
     * @return 처리한 데이터 수
     */
    @Transactional
    public List<PublicHolidayEntity> upsertPublicHolidays(String year, String country) {
        
        // 국가 존재 확인
        CountryEntity countryEntity = countryRepository.findByCountryCode(country);
        if (countryEntity == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        
        // Nager API 호출
        String url = String.format("%s/%s/%s", DataInitializer.NAGER_HOLIDAY_URL, year, country);
        PublicHoliday[] apiHolidays = restTemplate.getForObject(url, PublicHoliday[].class);
        if (apiHolidays == null) {
            logger.error("upsertPublicHolidays nager url is error :: {}", url);
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        // 기존 DB 데이터 조회
        List<PublicHolidayEntity> dbHolidays = holidayRepository.findByHolidayYearAndCountry_CountryCode(year, country);

        // DB에 데이터가 없으면, API 데이터로 전부 insert
        if (dbHolidays.isEmpty()) {
            List<PublicHolidayEntity> savedEntities = mapAndSaveHolidays(apiHolidays, year, countryEntity);
            return savedEntities;    
        }

        //Api결과와 db결과가 일치하는지 date|name로 비교확인.
        if (apiHolidays.length == dbHolidays.size()) {

            // DB에 존재하는 date|name Set 생성
            Set<String> existingDateName = dbHolidays.stream()
                    .map(h -> h.getDate() + "|" + h.getName())
                    .collect(Collectors.toSet());

            // API에서 조회한 휴일의 date|name Set 생성
            Set<String> apiDateName = Arrays.stream(apiHolidays)
                    .map(h -> h.getDate() + "|" + h.getName())
                    .collect(Collectors.toSet());

            /**
             * 하나라도 일치하지 않으면, 전체 삭제 후 새로 insert
             * - Key가 없음으로 데이터를 찾을수없기 때문.
             */
            if (!existingDateName.equals(apiDateName)) {
                // year, country 조회된 데이터 전체 삭제(2025, KR)
                holidayRepository.deleteAll(dbHolidays);

                // 새 엔티티 생성 및 저장
                List<PublicHolidayEntity> savedEntities = mapAndSaveHolidays(apiHolidays, year, countryEntity);
                return savedEntities;
            } else {
                // Date+Name 모두 일치하면 기존 DB 그대로 반환
                return dbHolidays;
            }
            

        /* 1. Url조회값이 추가된것이 있다면, DB에 신규 휴일추가 (apiHolidays.length > dbHolidays.size()) */
        /* 2. Api값에서 DB에 있는 기존휴일이 사라짐 [DB요소 삭제] (apiHolidays.length < dbHolidays.size()) */
        } else {
            // API에서 내려온 date+name 조합
            Set<String> apiDateName = Arrays.stream(apiHolidays)
                    .map(h -> h.getDate() + "|" + h.getName())
                    .collect(Collectors.toSet());

            // DB에서 API에 없는 date+name 조합 필터링
            List<PublicHolidayEntity> toRemove = dbHolidays.stream()
                    .filter(h -> !apiDateName.contains(h.getDate() + "|" + h.getName()))
                    .collect(Collectors.toList());

            if (!toRemove.isEmpty()) {
                logger.info("DB Old Holiday to Delete (count={}):", toRemove.size());
                toRemove.forEach(h -> logger.info("date={}, name={}", h.getDate(), h.getName()));

                holidayRepository.deleteAll(toRemove);
                dbHolidays.removeAll(toRemove); // 반환용 리스트에서도 제거
            }

            // url에 새로운 데이터가 있으면 DB에 insert
            List<PublicHolidayEntity> newEntities = isNewEliment(apiHolidays, dbHolidays, countryEntity, year);
            if (!newEntities.isEmpty()) {

                logger.info("New Holiday DATA (count={}):", newEntities.size());
                newEntities.forEach(h -> logger.info("date={}, name={}", h.getDate(), h.getName()));

                holidayRepository.saveAll(newEntities);
                dbHolidays.addAll(newEntities); // 최종 반환용 리스트에 추가
            }
        }
        return dbHolidays;
    }


    /**
     * DB저장
     * @param apiHolidays
     * @param year
     * @param countryEntity
     * @return
     */
    private List<PublicHolidayEntity> mapAndSaveHolidays(PublicHoliday[] apiHolidays, String year, CountryEntity countryEntity) {

        // DB Model형태로 변경
        List<PublicHolidayEntity> entities = Arrays.stream(apiHolidays)
                .map(h -> {
                    PublicHolidayEntity e = new PublicHolidayEntity();
                    e.setDate(h.getDate());
                    e.setLocalName(h.getLocalName());
                    e.setName(h.getName());
                    e.setFixed(h.getFixed());
                    e.setGlobal(h.getGlobal());
                    e.setCounties(h.getCounties());
                    e.setLaunchYear(h.getLaunchYear());
                    e.setHolidayYear(year);
                    e.setCountry(countryEntity); // CountryEntity 매핑
                    return e;
                })
                .collect(Collectors.toList());

        holidayRepository.saveAll(entities);

        logger.info("all new Holiday Data regist (count={}):", entities.size());
        entities.forEach(h -> logger.info("date={}, name={}", h.getDate(), h.getName()));

        return entities;
    }


    /**
     * Url조회값이 추가된것이 있다면, DB에 신규 휴일추가
     */
    private List<PublicHolidayEntity> isNewEliment(PublicHoliday[] apiHolidays, List<PublicHolidayEntity> dbHolidays ,CountryEntity countryEntity, String year){
        // DB에서 date+name 조합 확인
            Set<String> existingDateName = dbHolidays.stream()
                    .map(h -> h.getDate() + "|" + h.getName()) // "2025-12-25|Christmas"
                    .collect(Collectors.toSet());

            // 새로 추가할 데이터만 필터링
            List<PublicHolidayEntity> newEntities = Arrays.stream(apiHolidays)
                    .filter(h -> !existingDateName.contains(h.getDate() + "|" + h.getName())) // DB에 없는 조합만
                    .map(h -> PublicHolidayEntity.builder()
                            .date(h.getDate())
                            .holidayYear(year)
                            .country(countryEntity)
                            .localName(h.getLocalName())
                            .name(h.getName())
                            .fixed(h.getFixed())
                            .global(h.getGlobal())
                            .counties(h.getCounties())
                            .launchYear(h.getLaunchYear())
                            .build())
                    .collect(Collectors.toList());

            return newEntities;
    }
}