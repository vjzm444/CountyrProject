package com.country.project.Controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.country.project.Service.CountryService;
import com.country.project.model.Country;
import com.country.project.model.PublicHolidayEntity;


@RestController
public class CountryController {

    @Autowired
    private CountryService countryService;

    @GetMapping("/countries")
    public Country[] getCountries() {
        return countryService.getAvailableCountries();
    }

    @GetMapping("/holidays")
    public List<PublicHolidayEntity> getHolidays() {
        String year = "2025";
        String country ="KR";
        return countryService.getPublicHolidays(year,country);
    }
}