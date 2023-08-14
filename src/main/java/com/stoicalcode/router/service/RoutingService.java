package com.stoicalcode.router.service;

import com.stoicalcode.router.exception.InvalidCountryException;
import com.stoicalcode.router.exception.PathNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class RoutingService {

    private final SearchCountryService searchCountryService;

    @Autowired
    public RoutingService(SearchCountryService searchCountryService) {
        this.searchCountryService = searchCountryService;
    }

    public List<String> findLandRoute(String origin, String destination)
            throws IOException, InvalidCountryException, PathNotFoundException {
        return searchCountryService.findLandRoute(origin, destination);
    }
}