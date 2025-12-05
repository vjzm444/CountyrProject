package com.country.project.model;


import lombok.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
//url에서조회후매핑
public class PublicHoliday {
    public String date;

    public String localName;
    public String name;
    public String countryCode;
    public Boolean fixed;
    public Boolean global;
    public String counties;
    public Integer launchYear;
}