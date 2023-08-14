package com.stoicalcode.router.service;

import com.stoicalcode.router.exception.InvalidCountryException;
import com.stoicalcode.router.exception.PathNotFoundException;
import com.stoicalcode.router.model.CountryDto;
import com.stoicalcode.router.model.CountryValidationResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class BreadthFirstSearchCountryService implements SearchCountryService {

    private static final String PATH_NOT_FOUND_ERROR = "Not possible land route from '%s' to '%s'";

    private final CountryService countryService;

    @Autowired
    public BreadthFirstSearchCountryService(CountryService countryService) {
        this.countryService = countryService;
    }

    @Override
    public List<String> findLandRoute(String origin, String destination)
            throws IOException, InvalidCountryException, PathNotFoundException {
        CountryValidationResponseDto validationResponse = countryService.validateCountries(origin, destination);
        CountryDto originCountry = validationResponse.originCountry();
        CountryDto destinationCountry = validationResponse.destinationCountry();
        Map<String, CountryDto> cca3ToCountryMap = validationResponse.cca3ToCountryMap();

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
                CountryDto neighborCountry = cca3ToCountryMap.get(neighborCode);
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
            throw new PathNotFoundException(String.format(PATH_NOT_FOUND_ERROR, origin, destination));
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
        for (int i = reversedPath.size() - 1; i >= 0; i--) {
            CountryDto country = reversedPath.get(i);
            route.add(country.getCca3());
        }

        showDebugCountryRouteInfo(originCountry, destinationCountry, reversedPath);
        return route;
    }

    private void showDebugCountryRouteInfo(CountryDto originCountry, CountryDto destinationCountry,
                                           List<CountryDto> reversedPath) {
        if (log.isDebugEnabled()) {
            List<String> fullRouteDebug = new ArrayList<>(reversedPath.size());
            for (int i = reversedPath.size() - 1; i >= 0; i--) {
                CountryDto country = reversedPath.get(i);
                fullRouteDebug.add(String.format("%s", countryService.getCountryNameWithCca3(country)));
            }

            log.debug("Found route from '{}' to '{}': {}", countryService.getCountryNameWithCca3(originCountry),
                    countryService.getCountryNameWithCca3(destinationCountry), String.join(" > ", fullRouteDebug));
        }
    }
}