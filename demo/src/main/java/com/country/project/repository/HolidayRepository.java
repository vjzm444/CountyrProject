package com.country.project.repository;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.country.project.model.PublicHolidayEntity;

/**
 * 국가,년도,휴일목록
 */
@Repository
public interface HolidayRepository extends JpaRepository<PublicHolidayEntity, Long> {
    
    //전체조회
    public List<PublicHolidayEntity> findAll();

    // year와 countryCode로 조회(페이징)
    Page<PublicHolidayEntity> findByHolidayYearAndCountry_CountryCode(String year, String countryCode, Pageable pageable);
    
    // year, countryCode로 조회
    List<PublicHolidayEntity> findByHolidayYearAndCountry_CountryCode(String year, String countryCode);
    
    // year, month, countryCode로 조회
    Page<PublicHolidayEntity> findByCountry_CountryCodeAndDateBetween(
        String countryCode, LocalDate startDate, LocalDate endDate, Pageable pageable);
}