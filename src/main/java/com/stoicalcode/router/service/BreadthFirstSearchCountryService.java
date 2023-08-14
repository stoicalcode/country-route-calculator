package com.stoicalcode.router.service;

import com.stoicalcode.router.exception.PathNotFoundException;
import com.stoicalcode.router.model.CountryDto;
import com.stoicalcode.router.model.CountryValidationResponseDto;
import com.stoicalcode.router.model.NameDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@Primary
@Service
public class BreadthFirstSearchCountryService implements SearchCountryService {

    private final CountryService countryService;

    @Autowired
    public BreadthFirstSearchCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    @Override
    public List<String> findLandRoute(String origin, String destination) throws IOException {
        CountryValidationResponseDto validationResponse = countryService.validateCountries(origin, destination);
        CountryDto originCountry = validationResponse.originCountry();
        CountryDto destinationCountry = validationResponse.destinationCountry();
        Map<String, CountryDto> codeToCountryMap = validationResponse.codeToCountryMap();

        Queue<CountryDto> queue = new LinkedList<>();
        Set<CountryDto> visited = new HashSet<>();
        Map<CountryDto, CountryDto> previousPaths = new HashMap<>();
        boolean foundPath = false;

        queue.add(originCountry);

        outerLoop: while (!queue.isEmpty()) {
            CountryDto currentCountry = queue.poll();
            visited.add(currentCountry);

            foundPath = currentCountry.equals(destinationCountry);
            if (foundPath) {
                break;
            }

            for (String neighborCode : currentCountry.getBorders()) {
                CountryDto neighborCountry = codeToCountryMap.get(neighborCode);
                if (!visited.contains(neighborCountry)) {
                    previousPaths.put(neighborCountry, currentCountry);

                    foundPath = neighborCountry.equals(destinationCountry);
                    if (foundPath) {
                        break outerLoop;
                    }

                    visited.add(neighborCountry);
                    queue.add(neighborCountry);
                }
            }
        }

        if (!foundPath) {
            throw new PathNotFoundException(String.format("not found land path from '%s' to '%s'", origin, destination));
        }

        return buildRoute(originCountry, destinationCountry, previousPaths);
    }

    private List<String> buildRoute(CountryDto originCountry, CountryDto destinationCountry,
                                    Map<CountryDto, CountryDto> previousPath) {
        List<CountryDto> reversedPath = new ArrayList<>();
        for (CountryDto country = destinationCountry; country != null; country = previousPath.get(country)) {
            reversedPath.add(country);
        }

        List<String> route = new ArrayList<>(reversedPath.size());
        List<String> fullRouteDebug = new ArrayList<>(reversedPath.size());
        for (int i = reversedPath.size() - 1; i >= 0; i--) {
            CountryDto country = reversedPath.get(i);
            route.add(country.getCca3());

            if (log.isDebugEnabled()) {
                fullRouteDebug.add(String.format("%s (%s)", country.getName().getCommon(), country.getCca3()));
            }
        }

        log.debug("Found route from '{}' to '{}': {}", getCountryName(originCountry), getCountryName(destinationCountry),
                String.join(" > ", fullRouteDebug));

        return route;
    }

    private String getCountryName(CountryDto country) {
        NameDto countryName = country.getName();
        if (countryName != null && countryName.getCommon() != null) {
            return countryName.getCommon();
        }

        return country.getCca3();
    }
}