package com.country.project.model.jsonModel;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * 국가목록
 * - Url에서 조회후 매핑 
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Country {

    public String countryCode;

    public String name;
}