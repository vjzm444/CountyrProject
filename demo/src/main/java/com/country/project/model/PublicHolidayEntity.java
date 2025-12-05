package com.country.project.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "holiday")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//Db테이블꺼
public class PublicHolidayEntity {
    @Id
    public String date;
    public String localName;
    public String name;
    public String countryCode;
    public Boolean fixed;
    public Boolean global;
    public String counties;
    public Integer launchYear;
}