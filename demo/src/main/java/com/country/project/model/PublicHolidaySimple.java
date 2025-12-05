package com.country.project.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PublicHolidaySimple {
    private String date;
    private String localName;
    private String name;
    private String countryCode;
}