package com.stoicalcode.router.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class RoutingService {

    private final SearchCountryService searchCountryService;

    @Autowired
    public RoutingService(SearchCountryService searchCountryService) {
        this.searchCountryService = searchCountryService;
    }

    public List<String> findLandRoute(String origin, String destination) throws IOException {
        return searchCountryService.findLandRoute(origin, destination);
    }
}