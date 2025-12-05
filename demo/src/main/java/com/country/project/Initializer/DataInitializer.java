package com.country.project.Initializer;


import com.country.project.model.PublicHoliday;
import com.country.project.model.PublicHolidayEntity;
import com.country.project.repository.PublicHolidayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final PublicHolidayRepository repository;
    private final RestTemplate restTemplate;

    public DataInitializer(PublicHolidayRepository repository) {
        this.repository = repository;
        this.restTemplate = new RestTemplate();
    }

    @PostConstruct
    public void init() {
        String url = "https://date.nager.at/api/v3/PublicHolidays/2025/KR";
        PublicHoliday[] holidays = restTemplate.getForObject(url, PublicHoliday[].class);

        if (holidays != null) {
            List<PublicHolidayEntity> entityList = new ArrayList<>();

            for (PublicHoliday h : holidays) {
                
                PublicHolidayEntity e = new PublicHolidayEntity();

                e.date = h.date;
                e.localName = h.localName;
                e.name = h.name;
                e.countryCode = h.countryCode;
                e.fixed = h.fixed;
                e.global = h.global;
                e.counties = h.counties;
                e.launchYear = h.launchYear;

                entityList.add(e);
            }

            repository.saveAll(entityList);
            logger.info("✅ PublicHoliday data saved to H2, count: " + entityList.size());
        }
    }

    //국가별로 축일을 설정한다
    public void AddCountryData(String[] countryCodes){

    }

    //최근 5년간 리턴한다
    
    public static String[] RecentYear(){
        int currentYear = Year.now().getValue(); // 현재 연도 2025
        int numberOfYears = 6; // 최근 5년 + 현재 연도

        String[] years = new String[numberOfYears];
        
        int startYear = currentYear - (numberOfYears - 1); // 2020
        for (int i = 0; i < numberOfYears; i++) {
            years[i] = String.valueOf(startYear + i); // 2020, 2021, ..., 2025
        }
        return years;
    }



}