package com.stoicalcode.router.model;

import java.util.Map;

public record CountryValidationResponseDto(CountryDto originCountry, CountryDto destinationCountry,
                                           Map<String, CountryDto> cca3ToCountryMap) {
}