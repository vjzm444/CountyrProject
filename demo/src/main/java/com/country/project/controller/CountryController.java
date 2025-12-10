package com.country.project.controller;


import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.country.project.common.DateValidator;
import com.country.project.config.exception.CustomException;
import com.country.project.config.exception.ErrorCode;
import com.country.project.model.CountryEntity;
import com.country.project.model.PublicHolidayEntity;
import com.country.project.service.CountryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * 국가별 휴일 관리
 */
@RestController
@Tag(name = "Country API", description = "국가 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class CountryController {

    //로거
    private static final Logger logger = LoggerFactory.getLogger(CountryController.class);

    //서비스
    @Autowired
    private CountryService countryService;
    
    /**
     * 국가코드 조회
     */
    @GetMapping("/countries")
    @Operation(summary = "모든 국가 조회", description = "이 API는 등록된 모든 국가 정보를 조회합니다. 예시: 국가 코드, 국가 이름 등")
    public List<CountryEntity> getCountries() {
        return countryService.getAvailableCountries();
    }
    
    /**
     * 휴일 조회
     *  - 특정연도 국가코드로 검색
     *      - month값은 선택사항
     * @param year
     * @param country
     * @param month
     * @param page
     * @return
     */
    @GetMapping("/holidays/{year}/{country}")
    @Operation(summary = "연도와 국가별 공휴일 조회", description = "이 API는 특정 연도와 국가의 공휴일 데이터를 반환합니다.")
    public Page<PublicHolidayEntity> getHolidays(
            @Parameter(description = "조회 할 연도(2020,2021,2022...)", example = "2025") @PathVariable("year") String year,
            @Parameter(description = "조회 할 국가 코드(KR,JP,CN...)", example = "KR") @PathVariable("country") String country,
            @Parameter(description = "조회 할 월(선택, 1~12) 사용안할시 빈값셋팅", example = "") @RequestParam(value = "month", required = false) Integer month,
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(value = "page", defaultValue = "0") int page) {
        logger.info("year: {}, month: {}, country: {}, page: {}", year, month, country, page);

        // 년,월이 올바른지?
        boolean isValid = DateValidator.validateYearAndMonth(year, month);
        if (!isValid) {
            throw new CustomException(ErrorCode.UNSUPPORTED_MEDIA_TYPE); //올바르지 않은 파라미터
        }

        try {    
            //DB 휴일조회
            Page<PublicHolidayEntity> holidays = countryService.getPublicHolidays(year, month, country, page);
            
            return holidays;
        } catch (ResponseStatusException ex) {
            logger.error("getHolidays Error :: "+ ex.getMessage());
           throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 삭제
     *  - 특정연도의 국가 대상
     */
    @DeleteMapping("/holidays/{year}/{country}")
    public ResponseEntity<Map<String, Integer>> deleteHolidays(
            @Parameter(description = "삭제 할 연도(2020,2021,2022...)", example = "2025") @PathVariable("year") String year,
            @Parameter(description = "삭제 할 국가 코드(KR,JP,CN...)", example = "KR") @PathVariable("country") String country) {
        // year값이 최근5년에 해당되는지?
        boolean isValid = DateValidator.validateYear(year);
        if (!isValid) {
            throw new CustomException(ErrorCode.UNSUPPORTED_MEDIA_TYPE); //올바르지 않은 파라미터
        }

        int deletedCnt = countryService.deleteHolidays(year, country);
        Map<String, Integer> result = new HashMap<>();
        result.put("count", deletedCnt);

        //삭제된 수 리턴
        return ResponseEntity.ok(result); // HTTP 200 + { "count": 15 }
    }

    /**
     * 신규등록 or 덮어쓰기
     * - 특정연도의 국가 대상
     * @param year
     * @param country
     * @return
     */
    @PostMapping("/upsert/{year}/{country}")
    @Operation(summary = "특정 연도·국가 공휴일 Upsert", description = "연도와 국가공휴일를 재호출하여 존재하면 덮어쓰고, 없으면 새로 추가")
    public List<PublicHolidayEntity> upsertHolidays(
            @Parameter(description = "연도", example = "2025") @PathVariable("year") String year,
            @Parameter(description = "국가 코드", example = "KR") @PathVariable("country") String country) {
            
        // year값이 최근5년에 해당되는지?
        boolean isValid = DateValidator.validateYear(year);
        if (!isValid) {
            throw new CustomException(ErrorCode.UNSUPPORTED_MEDIA_TYPE); // 올바르지 않은 파라미터
        }

        List<PublicHolidayEntity> result = countryService.upsertPublicHolidays(year, country);
        // 날짜 기준 오름차순 정렬
        result.sort(Comparator.comparing(PublicHolidayEntity::getDate));
        return result;
    }
}