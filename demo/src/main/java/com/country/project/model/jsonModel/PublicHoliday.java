package com.country.project.model.jsonModel;


import lombok.*;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * 휴일목록
 * - Url에서 조회후 매핑 
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PublicHoliday {
    public LocalDate date;

    public String localName;
    public String name;
    public String countryCode;
    public Boolean fixed;
    public Boolean global;
    public List<String> counties;
    public Integer launchYear;
}