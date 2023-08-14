package com.stoicalcode.router.service;

import com.stoicalcode.router.exception.InvalidCountryException;
import com.stoicalcode.router.exception.PathNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void shouldCallCountryRouteSearch_WhenCorrectParams() throws IOException, InvalidCountryException, PathNotFoundException {
        List<String> expected = Arrays.asList("CZE", "AUT", "ITA");

        when(mockSearchCountryService.findLandRoute(ORIGIN, DESTINATION)).thenReturn(expected);

        List<String> actual = sut.findLandRoute(ORIGIN, DESTINATION);

        assertThat(actual).isEqualTo(expected);
        verify(mockSearchCountryService, times(1)).findLandRoute(ORIGIN, DESTINATION);
        verifyNoMoreInteractions(mockSearchCountryService);
    }

    @Test
    void shouldHandleIOException() throws IOException, InvalidCountryException, PathNotFoundException {
        when(mockSearchCountryService.findLandRoute(ORIGIN, DESTINATION)).thenThrow(new IOException("simulated IOException"));

        assertThrows(IOException.class, () -> sut.findLandRoute(ORIGIN, DESTINATION));
        verify(mockSearchCountryService, times(1)).findLandRoute(ORIGIN, DESTINATION);
        verifyNoMoreInteractions(mockSearchCountryService);
    }

    @Test
    void shouldHandleAnyOtherException() throws IOException, InvalidCountryException, PathNotFoundException {
        when(mockSearchCountryService.findLandRoute(ORIGIN, DESTINATION)).thenThrow(new RuntimeException("Simulated RuntimeException"));

        assertThrows(RuntimeException.class, () -> sut.findLandRoute(ORIGIN, DESTINATION));
        verify(mockSearchCountryService, times(1)).findLandRoute(ORIGIN, DESTINATION);
        verifyNoMoreInteractions(mockSearchCountryService);
    }
}