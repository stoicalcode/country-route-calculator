package com.stoicalcode.router.service;

import java.io.IOException;
import java.util.List;

public interface SearchCountryService {

    List<String> findLandRoute(String origin, String destination) throws IOException;
}