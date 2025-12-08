package com.country.project.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * DB테이블과 매칭
 *  -국가정보
 */
@Entity
@Table(name = "country")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountryEntity {

    @Id
    public String countryCode; // PK

    public String name;

}
