package com.country.project.initializer;

import com.country.project.model.CountryEntity;
import com.country.project.model.PublicHolidayEntity;
import com.country.project.model.jsonModel.Country;
import com.country.project.model.jsonModel.PublicHoliday;
import com.country.project.repository.CountryRepository;
import com.country.project.repository.HolidayRepository;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
//서버가 올락갈때 자동실행
public class DataInitializer {

    // 로거
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    // 휴일 DB정보
    private final HolidayRepository holidayRepository;
    // 국가 DB정보
    private final CountryRepository countryRepository;

    private final RestTemplate restTemplate;
    //국가목록
    private final String nagerCountryUrl= "https://date.nager.at/api/v3/AvailableCountries";
    //축일정보
    private final String nagerHolidayUrl = "https://date.nager.at/api/v3/PublicHolidays";

    // 생성자
    public DataInitializer(HolidayRepository _holidayRepository, CountryRepository _countryRepository) {
        this.holidayRepository = _holidayRepository;
        this.countryRepository = _countryRepository;

        this.restTemplate = new RestTemplate();
    }

    /* 초기화 */
    @PostConstruct
    public void Init() {
        try {
            // 처리후 국가코드 목록
            String[] codesArray = GetCountryData();
            AddCountryHoliday(codesArray);
        } catch (Exception e) {
            logger.error("Init Error" + e.getMessage());
        }
        
    }

    // 국가코드 설정
    private String[] GetCountryData() {
        
        ResponseEntity<Country[]> response = restTemplate.getForEntity(nagerCountryUrl, Country[].class);
        Country[] countryList = response.getBody();
        
        List<CountryEntity> entityList = new ArrayList<>();

        for (Country h : countryList) {

            CountryEntity e = new CountryEntity();
            e.name = h.name;
            e.countryCode = h.countryCode;

            entityList.add(e);
        }
        // DB 저장
        countryRepository.saveAll(entityList);

        String[] codesArray = entityList.stream()
                .filter(x -> x.countryCode != null && !x.countryCode.trim().isEmpty()) // where
                .map(e -> e.countryCode) // select
                .toArray(String[]::new);

        return codesArray;
    }

    // 국가별로 축일을 설정
    private void AddCountryHoliday(String[] codes) {

        //최근 5년
        String[] years = RecentYear();
        List<PublicHolidayEntity> allEntities = new ArrayList<>();

        // 1) CountryEntity 전부 조회해서 Map으로 캐싱 (DB hit 최소화)
        Map<String, CountryEntity> countryMap = countryRepository.findAll()
        .stream()
        .collect(Collectors.toMap(
                c -> c.countryCode,   // key
                c -> c                // value
        ));

        for (String code : codes) {
            logger.info("country start ::: " + code);

            for (String year : years) {
                // API 요청 URL
                
                String url = String.format("%s/%s/%s", nagerHolidayUrl, year, code);
                //logger.info("url ::: " + url);

                PublicHoliday[] holidays = restTemplate.getForObject(url, PublicHoliday[].class);

                if (holidays == null || holidays.length == 0) {
                    continue;
                }

                // 3) PublicHoliday → Entity 매핑
                for (PublicHoliday h : holidays) {

                    PublicHolidayEntity e = new PublicHolidayEntity();
                    e.date = h.date;
                    e.localName = h.localName;
                    e.name = h.name;
                    e.fixed = h.fixed;
                    e.global = h.global;
                    e.counties = h.counties;
                    e.launchYear = h.launchYear;
                    e.holidayYear = year;

                    // CountryEntity 매핑
                    CountryEntity country = countryMap.get(h.countryCode);
                    e.country = country;

                    allEntities.add(e);
                }
            }
        }

        // 4) 모든 반복이 끝난 뒤 DB에 한 번에 저장
        holidayRepository.saveAll(allEntities);

        logger.info("Saved holidays: " + allEntities.size());
    }

    // 현재년도 기준 최근 5년을 리턴
    public static String[] RecentYear() {
        int currentYear = Year.now().getValue(); // 현재 연도 2025
        int numberOfYears = 5; // 최근 5년 + 현재 연도

        String[] years = new String[numberOfYears];

        int startYear = currentYear - (numberOfYears - 1); // 2020
        for (int i = 0; i < numberOfYears; i++) {
            years[i] = String.valueOf(startYear + i); // 2020, 2021, ..., 2025
        }
        return years;
    }

}