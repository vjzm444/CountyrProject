package com.country.project.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.country.project.model.*;

@Repository
public interface CountryRepository extends JpaRepository<CountryEntity, String> {
    
    //전체조회
    public List<CountryEntity> findAll();
    //나라코드로 조회
    public CountryEntity findByCountryCode(String code);
}