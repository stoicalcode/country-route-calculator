package com.stoicalcode.router.service;

import com.stoicalcode.router.exception.InvalidCountryException;
import com.stoicalcode.router.exception.PathNotFoundException;

import java.io.IOException;
import java.util.List;

public interface SearchCountryService {

    List<String> findLandRoute(String origin, String destination)
            throws IOException, InvalidCountryException, PathNotFoundException;
}