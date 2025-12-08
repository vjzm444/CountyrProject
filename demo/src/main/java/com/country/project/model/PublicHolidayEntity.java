package com.country.project.model;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;


/**
 * DB테이블과 매칭
 *  -나라별 휴일
 */
@Entity
@Table(name = "holiday")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicHolidayEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // PK

    
    public LocalDate date;
    public String localName;
    public String name;
    public Boolean fixed;
    public Boolean global;
    public List<String> counties;
    public Integer launchYear;

    //프로젝트에서 관리를 위해 따로 년도 추가
    public String holidayYear;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_code", referencedColumnName = "countryCode")
    @JsonIgnore
    public CountryEntity country; // FK
    
    
}