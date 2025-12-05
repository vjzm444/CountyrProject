package com.country.project.Controller;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.country.project.Service.CountryService;
import com.country.project.model.Country;
import com.country.project.model.PublicHolidayEntity;


@RestController
public class CountryController {

    private static final Logger logger = LoggerFactory.getLogger(CountryController.class);

    @Autowired
    private CountryService countryService;

    @GetMapping("/countries")
    public Country[] getCountries() {
        return countryService.getAvailableCountries();
    }

    @GetMapping("/holidays/{year}/{country}")
    public List<PublicHolidayEntity> getHolidays(
    @PathVariable("year") String year,
    @PathVariable("country") String country) {
        logger.info("year: {}, country: {}", year, country);
        return countryService.getPublicHolidays(year,country);
    }
}