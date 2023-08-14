package com.stoicalcode.router.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoutingServiceTest {

    private static final String ORIGIN = "CZE";
    private static final String DESTINATION = "ITA";

    @Mock
    private SearchCountryService mockSearchCountryService;

    private RoutingService sut;

    @BeforeEach
    void setUp() {
        sut = new RoutingService(mockSearchCountryService);
    }

    @Test
    void shouldCallCountryRouteSearchWithCorrectParams() throws IOException {
        List<String> expectedRoute = Arrays.asList("CZE", "AUT", "ITA");

        when(mockSearchCountryService.findLandRoute(ORIGIN, DESTINATION)).thenReturn(expectedRoute);

        List<String> actualRoute = sut.findLandRoute(ORIGIN, DESTINATION);

        assertEquals(expectedRoute, actualRoute);
        verify(mockSearchCountryService, times(1)).findLandRoute(ORIGIN, DESTINATION);
        verifyNoMoreInteractions(mockSearchCountryService);
    }

    @Test
    void shouldHandleIOExceptionFromCountryRouteSearch() throws IOException {
        when(mockSearchCountryService.findLandRoute(ORIGIN, DESTINATION)).thenThrow(new IOException("simulated IOException"));

        assertThrows(IOException.class, () -> sut.findLandRoute(ORIGIN, DESTINATION));
        verify(mockSearchCountryService, times(1)).findLandRoute(ORIGIN, DESTINATION);
        verifyNoMoreInteractions(mockSearchCountryService);
    }

    @Test
    void shouldHandleAnyOtherExceptionFromCountryRouteSearch() throws IOException {
        when(mockSearchCountryService.findLandRoute(ORIGIN, DESTINATION)).thenThrow(new RuntimeException("Simulated RuntimeException"));

        assertThrows(RuntimeException.class, () -> sut.findLandRoute(ORIGIN, DESTINATION));
        verify(mockSearchCountryService, times(1)).findLandRoute(ORIGIN, DESTINATION);
        verifyNoMoreInteractions(mockSearchCountryService);
    }
}