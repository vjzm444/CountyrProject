package com.country.project.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.country.project.model.*;

@Repository
public interface PublicHolidayRepository extends JpaRepository<PublicHolidayEntity, String> {
    
    public List<PublicHolidayEntity> findAll();
}