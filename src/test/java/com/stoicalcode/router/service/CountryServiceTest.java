package com.stoicalcode.router.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stoicalcode.router.exception.InvalidCountryException;
import com.stoicalcode.router.exception.PathNotFoundException;
import com.stoicalcode.router.model.CountryDto;
import com.stoicalcode.router.model.CountryValidationResponseDto;
import com.stoicalcode.router.model.NameDto;
import com.stoicalcode.router.model.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CountryServiceTest {

    private static final String COUNTRY_DATA_URL = "https://dummyurl.com/";

    private CountryService sut;

    @Mock
    private ObjectMapper mockObjectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sut = new CountryService();
        sut.setObjectMapper(mockObjectMapper);
        sut.setCountryDataUrl(COUNTRY_DATA_URL);
    }

    @Test
    void shouldGetValidResponse_whenValidCountries() throws IOException, InvalidCountryException, PathNotFoundException {
        String origin = "USA";
        String destination = "CAN";

        NameDto usaName = NameDto.builder().common("United States").official("United States of America").build();
        NameDto canadaName = NameDto.builder().common("Canada").official("Canada").build();

        CountryDto originCountry = CountryDto.builder()
                .cca3(origin).name(usaName).borders(List.of("CAN", "MEX")).region(Region.Americas).build();
        CountryDto destinationCountry = CountryDto.builder()
                .cca3(destination).name(canadaName).borders(List.of("USA")).region(Region.Americas).build();
        CountryDto[] countries = {originCountry, destinationCountry};

        when(mockObjectMapper.readValue(any(URL.class), eq(CountryDto[].class))).thenReturn(countries);

        CountryValidationResponseDto response = sut.validateCountries(origin, destination);

        assertThat(response).isNotNull();
        assertThat(response.originCountry()).isEqualTo(originCountry);
        assertThat(response.destinationCountry()).isEqualTo(destinationCountry);
        assertThat(response.cca3ToCountryMap()).isNotNull();
        assertThat(response.cca3ToCountryMap().get("USA")).isEqualTo(originCountry);
        assertThat(response.cca3ToCountryMap().get("CAN")).isEqualTo(destinationCountry);
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidCountriesArgumentsProvider.class)
    void shouldThrowInvalidCountryException_whenInvalidCountries(String origin, String destination,
                                                                 String expectedMessage) throws IOException {
        NameDto usaName = NameDto.builder().common("United States").official("United States of America").build();
        NameDto canadaName = NameDto.builder().common("Canada").official("Canada").build();

        CountryDto originCountry = CountryDto.builder()
                .cca3("USA").name(usaName).borders(List.of("CAN", "MEX")).region(Region.Americas).build();
        CountryDto destinationCountry = CountryDto.builder()
                .cca3("CAN").name(canadaName).borders(List.of("USA")).region(Region.Americas).build();
        CountryDto[] countries = {originCountry, destinationCountry};

        when(mockObjectMapper.readValue(any(URL.class), eq(CountryDto[].class))).thenReturn(countries);

        InvalidCountryException exception = assertThrows(InvalidCountryException.class,
                () -> sut.validateCountries(origin, destination));

        assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    }

    static class InvalidCountriesArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("INVALID_CCA3_USA", "CAN", "invalid origin country: 'INVALID_CCA3_USA'"),
                    Arguments.of("USA", "INVALID_CCA3_CAN", "invalid destination country: 'INVALID_CCA3_CAN'"),
                    Arguments.of("INVALID_CCA3_USA", "INVALID_CCA3_CAN",
                            "invalid origin country: 'INVALID_CCA3_USA', invalid destination country: 'INVALID_CCA3_CAN'"),
                    Arguments.of("CAN", "CAN", "origin and destination countries are the same: 'CAN'")
            );
        }
    }

    @Test
    void shouldThrowPathNotFoundException_whenNoRegionsConnected() throws IOException {
        String origin = "USA";
        String destination = "ITA";

        NameDto usaName = NameDto.builder().common("United States").official("United States of America").build();
        NameDto italyName = NameDto.builder().common("Italy").official("Italian Republic").build();

        CountryDto originCountry = CountryDto.builder()
                .cca3(origin).name(usaName).borders(List.of("CAN", "MEX")).region(Region.Americas).build();
        CountryDto destinationCountry = CountryDto.builder()
                .cca3(destination).name(italyName).borders(Collections.emptyList()).region(Region.Europe).build();
        CountryDto[] countries = {originCountry, destinationCountry};

        when(mockObjectMapper.readValue(any(URL.class), eq(CountryDto[].class))).thenReturn(countries);

        PathNotFoundException exception = assertThrows(PathNotFoundException.class,
                () -> sut.validateCountries(origin, destination));

        String expectedMessage = "origin and destination not connected by land:";
        assertThat(exception.getMessage()).contains(expectedMessage);
    }

    @Test
    void shouldThrowPathNotFoundException_whenNoBorders() throws IOException {
        String origin = "USA";
        String destination = "LCA";

        NameDto usaName = NameDto.builder().common("United States").official("United States of America").build();
        NameDto saintLuciaName = NameDto.builder().common("Saint Lucia").official("Saint Lucia").build();

        CountryDto originCountry = CountryDto.builder()
                .cca3(origin).name(usaName).borders(List.of("MEX", "CAN")).region(Region.Americas).build();
        CountryDto destinationCountry = CountryDto.builder()
                .cca3(destination).name(saintLuciaName).borders(Collections.emptyList()).region(Region.Americas).build();
        CountryDto[] countries = {originCountry, destinationCountry};

        when(mockObjectMapper.readValue(any(URL.class), eq(CountryDto[].class))).thenReturn(countries);

        PathNotFoundException exception = assertThrows(PathNotFoundException.class,
                () -> sut.validateCountries(origin, destination));

        String expectedMessage = "countries has no borders";
        assertThat(exception.getMessage()).contains(expectedMessage);
    }
}

