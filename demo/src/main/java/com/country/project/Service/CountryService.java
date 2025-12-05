package com.country.project.Service;


import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.country.project.model.Country;
import com.country.project.model.PublicHoliday;
import com.country.project.model.PublicHolidayEntity;
import com.country.project.repository.PublicHolidayRepository;

import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

@Service
public class CountryService {

    //로거
    private static final Logger logger = LoggerFactory.getLogger(CountryService.class);
    //템플릿
    private final RestTemplate restTemplate = new RestTemplate();

     @Autowired
    private PublicHolidayRepository repository;
    
    //국가조회(작업중)
    public Country[] getAvailableCountries() {
        String listUrl = "https://date.nager.at/api/v3/AvailableCountries";
        ResponseEntity<Country[]> response = restTemplate.getForEntity(listUrl, Country[].class);
        return response.getBody();
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
            logger.info("✅ PublicHoliday data saved to H2, count: " + holidays.length);
        }
    }

    // DB 조회
    public PublicHoliday[] getAllHolidays(String year, String cuntry) {
        return repository.findAll().toArray(new PublicHoliday[0]);
    }

    //쉬는날 조회
    public List<PublicHolidayEntity> getPublicHolidays(String year, String country) {
        List<PublicHolidayEntity> holidays = repository.findAll();

        if (holidays != null && !holidays.isEmpty()) {
            logger.info("PublicHoliday API called, number of items: {}", holidays.size());
        } else {
            logger.warn("PublicHoliday API nodata");
        }

        return holidays;  // 배열 대신 List 반환
    }
}