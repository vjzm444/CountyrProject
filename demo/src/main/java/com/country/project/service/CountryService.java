package com.country.project.service;


import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.country.project.config.CustomException;
import com.country.project.config.ErrorCode;
import com.country.project.model.CountryEntity;
import com.country.project.model.PublicHolidayEntity;
import com.country.project.repository.CountryRepository;
import com.country.project.repository.HolidayRepository;

@Service
public class CountryService {

    //로거
    private static final Logger logger = LoggerFactory.getLogger(CountryService.class);
    
    @Autowired
    private HolidayRepository holidayRepository;
    @Autowired
    private CountryRepository countryRepository;
    
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
     * 쉬는날 조회
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
            
            logger.info("month: {}, startDate: {}, endDate: {}", month, startDate, endDate);

            holidays = holidayRepository.findByCountry_CountryCodeAndDateBetween(country, startDate, endDate, pageable);
        } else {
            // month가 없으면 기존처럼 연도+국가 기준 조회
            holidays = holidayRepository.findByHolidayYearAndCountry_CountryCode(year, country, pageable);
        }

        return holidays;
    }
}