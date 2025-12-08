package com.country.project.service;


import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.country.project.config.CustomException;
import com.country.project.config.ErrorCode;
import com.country.project.model.CountryEntity;
import com.country.project.model.PublicHolidayEntity;
import com.country.project.repository.CountryRepository;
import com.country.project.repository.HolidayRepository;

import java.util.List;
import org.slf4j.Logger;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;

@Service
public class CountryService {

    //로거
    private static final Logger logger = LoggerFactory.getLogger(CountryService.class);
    
    @Autowired
    private HolidayRepository holidayRepository;
    @Autowired
    private CountryRepository countryRepository;
    
    /**
     * 국가조회
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
     * 쉬는날 조회
     * @param year
     * @param country
     * @return
     */
    public Page<PublicHolidayEntity> getPublicHolidays(String year, String country,int page) {
        CountryEntity codeSheachResult = countryRepository.findByCountryCode(country);
        //DB에 나라코드가 없음
        if(codeSheachResult == null){
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        int pageSize = 5;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("date").ascending());
        Page<PublicHolidayEntity> holidays = holidayRepository.findByHolidayYearAndCountry_CountryCode(year, country, pageable);

        //로그만
        if (holidays != null && !holidays.isEmpty()) {
            //logger.info("PublicHoliday API called, number of items: {}", holidays.size());
        } else {
            logger.warn("PublicHoliday API no data");
        }

        return holidays;  // 배열 대신 List 반환
    }
}