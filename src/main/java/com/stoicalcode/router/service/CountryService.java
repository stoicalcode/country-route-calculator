package com.stoicalcode.router.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stoicalcode.router.model.CountryDto;
import com.stoicalcode.router.model.CountryValidationResponseDto;
import com.stoicalcode.router.model.Region;
import com.stoicalcode.router.exception.InvalidCountryException;
import com.stoicalcode.router.exception.PathNotFoundException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Service

public class CountryService {
    private static final String INVALID_ORIGIN_COUNTRY_ERR0R = "invalid origin country: '%s'";
    private static final String INVALID_DESTINATION_COUNTRY_ERROR = "invalid destination country: '%s'";
    private static final String SAME_ORIGIN_AND_DESTINATION_ERROR = "origin and destination countries are the same: '%s'";
    private static final String REGIONS_NOT_CONNECTED_BY_LAND_ERROR = "origin and destination not connected by land: '%s' (%s region), '%s' (%s region)";
    private static final String COUNTRY_WITHOUT_LAND_BORDERS_ERROR = "origin '%s' or destination '%s' countries has no borders";

    @Value("${country-data-url}")
    @Setter
    private String countryDataUrl;

    @Setter
    private ObjectMapper objectMapper = new ObjectMapper();

    public CountryValidationResponseDto validateCountries(String origin, String destination) throws IOException {
        origin = origin.toUpperCase();
        destination = destination.toUpperCase();
        List<CountryDto> allCountries = getCountriesFromUrl();

        validateOriginAndDestination(origin, destination, allCountries);
        return validatePathBetweenOriginAndDestination(origin, destination, allCountries);
    }

    private List<CountryDto> getCountriesFromUrl() throws IOException {
        try {
            CountryDto[] countries = objectMapper.readValue(new URL(countryDataUrl), CountryDto[].class);
            return Arrays.asList(countries);
        } catch (IOException e) {
            log.error("Could not get countries from URL '{}': ", countryDataUrl, e);
            throw e;
        }
    }

    private void validateOriginAndDestination(String origin, String destination, List<CountryDto> allCountries) {
        List<String> errors = new ArrayList<>();

        Set<String> validCountryCodes = allCountries.stream().map(CountryDto::getCca3).collect(toSet());
        boolean validOriginCountry = validCountryCodes.contains(origin);
        boolean validDestinationCountry = validCountryCodes.contains(destination);

        if (!validOriginCountry) {
            errors.add(String.format(INVALID_ORIGIN_COUNTRY_ERR0R, origin));
        }

        if (!validDestinationCountry) {
            errors.add(String.format(INVALID_DESTINATION_COUNTRY_ERROR, destination));
        }

        if (validOriginCountry && validDestinationCountry && origin.equals(destination)) {
            errors.add(String.format(SAME_ORIGIN_AND_DESTINATION_ERROR, destination));
        }

        if (!CollectionUtils.isEmpty(errors)) {
            throw new InvalidCountryException(String.join(", ", errors));
        }
    }

    private CountryValidationResponseDto validatePathBetweenOriginAndDestination(String origin, String destination,
                                                                                 List<CountryDto> allCountries) {
        CountryDto originCountry = null;
        CountryDto destinationCountry = null;
        Map<String, CountryDto> cca3ToCountryMap = new HashMap<>();

        List<String> errors = new ArrayList<>();
        Optional<CountryDto> originCountryOpt = getCountryByCode(origin, allCountries);
        Optional<CountryDto> destinationCountryOpt = getCountryByCode(destination, allCountries);

        if (originCountryOpt.isPresent() && destinationCountryOpt.isPresent()) {
            originCountry = originCountryOpt.get();
            destinationCountry = destinationCountryOpt.get();

            Region originRegion = originCountry.getRegion();
            Region destinationRegion = destinationCountry.getRegion();

            if (!originRegion.isConnectedWith(destinationRegion)) {
                errors.add(String.format(REGIONS_NOT_CONNECTED_BY_LAND_ERROR, origin, originRegion,
                        destination, destinationRegion));
            } else if (isAnyCountryWithoutBorders(originCountry, destinationCountry)) {
                errors.add(String.format(COUNTRY_WITHOUT_LAND_BORDERS_ERROR, origin, destination));
            } else {
                cca3ToCountryMap = allCountries.stream().collect(toMap(CountryDto::getCca3, Function.identity()));
            }
        }

        if (CollectionUtils.isEmpty(errors)) {
            return new CountryValidationResponseDto(originCountry, destinationCountry, cca3ToCountryMap);
        }

        throw new PathNotFoundException(String.join(", ", errors));
    }

    private Optional<CountryDto> getCountryByCode(String cca3Code, List<CountryDto> countries) {
        return countries.stream()
                .filter(c -> c.getCca3().equalsIgnoreCase(cca3Code))
                .findFirst();
    }

    private boolean isAnyCountryWithoutBorders(CountryDto c1, CountryDto c2) {
        return !c1.equals(c2) && (
                CollectionUtils.isEmpty(c1.getBorders()) ||
                        CollectionUtils.isEmpty(c2.getBorders())
        );
    }
}