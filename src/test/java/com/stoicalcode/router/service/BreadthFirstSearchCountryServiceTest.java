package com.stoicalcode.router.service;

import com.stoicalcode.router.exception.PathNotFoundException;
import com.stoicalcode.router.model.CountryDto;
import com.stoicalcode.router.model.CountryValidationResponseDto;
import com.stoicalcode.router.model.NameDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BreadthFirstSearchCountryServiceTest {

    @Mock
    private CountryService mockCountryService;

    private BreadthFirstSearchCountryService sut;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sut = new BreadthFirstSearchCountryService(mockCountryService);
    }

    @Test
    void shouldFindValidLandRoute() throws IOException {
        NameDto czechiaName = NameDto.builder().common("Czechia").official("Czech Republic").build();
        NameDto austriaName = NameDto.builder().common("Austria").official("Republic of Austria").build();
        NameDto italyName = NameDto.builder().common("Italy").official("Italian Republic").build();

        CountryDto originCountry = CountryDto.builder()
                .cca3("CZE").name(czechiaName).borders(Collections.singletonList("AUT")).build();
        CountryDto intermediateCountry = CountryDto.builder()
                .cca3("AUT").name(austriaName).borders(Collections.singletonList("ITA")).build();
        CountryDto destinationCountry = CountryDto.builder()
                .cca3("ITA").name(italyName).borders(Collections.emptyList()).build();

        Map<String, CountryDto> codeToCountryMap = Map.of("CZE", originCountry, "AUT", intermediateCountry, "ITA", destinationCountry);
        CountryValidationResponseDto validationResponse = new CountryValidationResponseDto(
                originCountry, destinationCountry, codeToCountryMap);

        when(mockCountryService.validateCountries("CZE", "ITA")).thenReturn(validationResponse);

        List<String> actual = sut.findLandRoute("CZE", "ITA");
        List<String> expectedRoute = Arrays.asList("CZE", "AUT", "ITA");

        assertThat(actual).isEqualTo(expectedRoute);
    }

    @Test
    void shouldThrowPathNotFoundException() throws IOException {
        CountryDto originCountry = CountryDto.builder()
                .cca3("C1").borders(Collections.emptyList()).build();
        CountryDto destinationCountry = CountryDto.builder()
                .cca3("C2").borders(Collections.emptyList()).build();

        Map<String, CountryDto> codeToCountryMap = Map.of("C1", originCountry, "C2", destinationCountry);
        CountryValidationResponseDto validationResponse = new CountryValidationResponseDto(
                originCountry, destinationCountry, codeToCountryMap);

        when(mockCountryService.validateCountries("C1", "C2")).thenReturn(validationResponse);

        assertThrows(PathNotFoundException.class, () -> sut.findLandRoute("C1", "C2"));
    }
}