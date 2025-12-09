package com.country.project.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.country.project.model.*;

/**
 * 국가코드
 */
@Repository
public interface CountryRepository extends JpaRepository<CountryEntity, String> {
    
    //전체조회
    public List<CountryEntity> findAll();

    //나라코드 조회[벨리데이션용]
    public CountryEntity findByCountryCode(String code);
}