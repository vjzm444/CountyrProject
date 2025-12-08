package com.country.project.controller;


import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.country.project.service.CountryService;
import com.country.project.config.CustomException;
import com.country.project.config.ErrorCode;
import com.country.project.initializer.DataInitializer;
import com.country.project.model.CountryEntity;
import com.country.project.model.PublicHolidayEntity;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.data.domain.Page;

@RestController
@Tag(name = "Country API", description = "국가 관련 API")
public class CountryController {
    //로거
    private static final Logger logger = LoggerFactory.getLogger(CountryController.class);
    //서비스
    @Autowired
    private CountryService countryService;
    
    @GetMapping("/countries")
    @Operation(summary = "모든 국가 조회", description = "이 API는 등록된 모든 국가 정보를 조회합니다. 예시: 국가 코드, 국가 이름 등")
    public List<CountryEntity> getCountries() {
        return countryService.getAvailableCountries();
    }

    
    @GetMapping("/holidays/{year}/{country}")
    @Operation(summary = "연도와 국가별 공휴일 조회", description = "이 API는 특정 연도와 국가의 공휴일 데이터를 반환합니다.")
    public Page<PublicHolidayEntity> getHolidays(
            @Parameter(description = "조회할 연도(2020,2021,2022...)", example = "2025")
            @PathVariable("year") String year,
            @Parameter(description = "조회할 국가 코드(KR,JP,CN...)", example = "KR")
            @PathVariable("country") String country,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page
        ) {

        logger.info("year: {}, country: {}, page: {}", year, country, page);

        try {
            String[] recentYears = DataInitializer.RecentYear();

            // 입력year값이 최근5년인지?
            boolean isValidYear = Arrays.asList(recentYears).contains(year);
            if (!isValidYear) {
                throw new CustomException(ErrorCode.UNSUPPORTED_MEDIA_TYPE); //옳바르지 않은 파라미터
            }

            //DB 휴일조회
            Page<PublicHolidayEntity> holidays = countryService.getPublicHolidays(year, country, page);
            
            return holidays;
        } catch (ResponseStatusException ex) {
           throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}