package com.country.project.controller;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.country.project.service.CountryService;
import com.country.project.model.Country;
import com.country.project.model.PublicHolidayEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@Tag(name = "Country API", description = "국가 관련 API")
public class CountryController {

    private static final Logger logger = LoggerFactory.getLogger(CountryController.class);

    @Autowired
    private CountryService countryService;
    
    @GetMapping("/countries")
    @Operation(
        summary = "모든 국가 조회",
        description = "이 API는 등록된 모든 국가 정보를 조회합니다. 예시: 국가 코드, 국가 이름 등")
    public Country[] getCountries() {
        return countryService.getAvailableCountries();
    }

    @Operation(summary = "연도와 국가별 공휴일 조회",
               description = "이 API는 특정 연도와 국가의 공휴일 데이터를 반환합니다.")
    @GetMapping("/holidays/{year}/{country}")
    public List<PublicHolidayEntity> getHolidays(
            @Parameter(description = "조회할 연도(2020,2021,2022...)", example = "2025")
            @PathVariable("year") String year,
            @Parameter(description = "조회할 국가 코드(KR,JP,CN...)", example = "KR")
            @PathVariable("country") String country) {

        logger.info("year: {}, country: {}", year, country);
        return countryService.getPublicHolidays(year,country);
    }
}