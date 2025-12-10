package com.country.project.initializer;

import com.country.project.common.DateValidator;
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
    private static final String NAGER_COUNTRY_URL= "https://date.nager.at/api/v3/AvailableCountries";
    //축일정보
    public static final String NAGER_HOLIDAY_URL = "https://date.nager.at/api/v3/PublicHolidays";

    // 생성자
    public DataInitializer(HolidayRepository _holidayRepository, CountryRepository _countryRepository) {
        this.holidayRepository = _holidayRepository;
        this.countryRepository = _countryRepository;

        this.restTemplate = new RestTemplate();
    }

    // 초기화
    @PostConstruct
    public void dataInit() {
        try {
            // 처리후 국가코드 목록
            String[] codesArray = getCountryData();
            addCountryHoliday(codesArray);
        } catch (Exception e) {
            logger.error("Init Error" + e.getMessage());
        }
        
    }

    /**
     * 국가코드 설정
     * @return
     */
    private String[] getCountryData() {
        
        ResponseEntity<Country[]> response = restTemplate.getForEntity(NAGER_COUNTRY_URL, Country[].class);
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

    /**
     * 국가별로 축일을 설정
     */
    private void addCountryHoliday(String[] codes) {

        //최근 5년
        String[] years = DateValidator.recentYear();
        List<PublicHolidayEntity> allEntities = new ArrayList<>();

        // CountryEntity 전부 조회해서 Map으로 캐싱 (DB hit 최소화)
        Map<String, CountryEntity> countryMap = countryRepository.findAll()
        .stream()
        .collect(Collectors.toMap(
                c -> c.countryCode,   // key
                c -> c                // value
        ));
        
        logger.info("Initializer start. Countrys Count: {}. Please wait. Processing.....",codes.length);

        for (String code : codes) {
            for (String year : years) {
                // API 요청 URL
                
                String url = String.format("%s/%s/%s", NAGER_HOLIDAY_URL, year, code);
                //logger.info("url ::: " + url);

                PublicHoliday[] holidays = restTemplate.getForObject(url, PublicHoliday[].class);

                if (holidays == null || holidays.length == 0) {
                    continue;
                }

                // PublicHoliday → Entity 매핑
                for (PublicHoliday h : holidays) {
                    CountryEntity country = countryMap.get(h.getCountryCode()); // country 매핑

                    PublicHolidayEntity e = PublicHolidayEntity.builder()
                            .date(h.getDate())
                            .holidayYear(year)
                            .country(country)
                            .localName(h.getLocalName())
                            .name(h.getName())
                            .fixed(h.getFixed())
                            .global(h.getGlobal())
                            .counties(h.getCounties())
                            .launchYear(h.getLaunchYear())
                            // .types(h.getTypes()) // 필요하면 추가
                            .build();

                    allEntities.add(e);
                }

            }
        }

        // 모든 반복이 끝난 뒤 DB에 한 번에 저장
        holidayRepository.saveAll(allEntities);

        logger.info("Initializer End. Saved holidays: " + allEntities.size());
    }

}