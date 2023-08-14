package com.stoicalcode.router.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryDto {
    private NameDto name; // friendly identifier for the country (see @Name.common) TODO
    private String cca3; // ID, country code
    private Region region; // Region (Africa, America, ...)
    @EqualsAndHashCode.Exclude
    private List<String> borders; // List of neighboring country codes
}