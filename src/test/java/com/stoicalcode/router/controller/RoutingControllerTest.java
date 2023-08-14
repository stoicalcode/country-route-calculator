package com.stoicalcode.router.controller;

import com.stoicalcode.router.exception.InvalidCountryException;
import com.stoicalcode.router.exception.PathNotFoundException;
import com.stoicalcode.router.model.RouteResponseDto;
import com.stoicalcode.router.service.RoutingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoutingControllerTest {

    @Mock
    private RoutingService mockRoutingService;

    private RoutingController sut;

    @BeforeEach
    void setUp() {
        sut = new RoutingController(mockRoutingService);
    }

    @Test
    void testFindLandRoute_whenValid() throws IOException, InvalidCountryException, PathNotFoundException {
        List<String> route = Arrays.asList("CZE", "AUT", "ITA");
        when(mockRoutingService.findLandRoute("CZE", "ITA")).thenReturn(route);

        ResponseEntity<?> response = sut.findLandRoute("CZE", "ITA");
        RouteResponseDto routeResponse = (RouteResponseDto) response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(routeResponse.getRoute()).isEqualTo(route);
        verify(mockRoutingService, times(1)).findLandRoute("CZE", "ITA");
        verifyNoMoreInteractions(mockRoutingService);
    }

    @Test
    public void testFindLandRoute_whenInvalidCountryException() {
        when(sut.findLandRoute(any(), any())).thenThrow(new InvalidCountryException("Invalid country"));

        ResponseEntity<?> response = sut.findLandRoute("InvalidOrigin", "Destination");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody() instanceof String).isTrue();
        assertThat(response.getBody()).isEqualTo("Invalid country");
    }

    @Test
    public void testFindLandRoute_whenPathNotFoundException() {
        when(sut.findLandRoute(any(), any())).thenThrow(new PathNotFoundException("Path not found"));

        ResponseEntity<?> response = sut.findLandRoute("Origin", "NotFoundDestination");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody() instanceof String).isTrue();
        assertThat(response.getBody()).isEqualTo("Path not found");
    }

    @Test
    public void testFindLandRoute_whenIOException() {
        when(sut.findLandRoute(any(), any())).thenThrow(new IOException("Internal error"));

        ResponseEntity<?> response = sut.findLandRoute("Origin", "Destination");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody() instanceof String).isTrue();
        assertThat(response.getBody()).isEqualTo("Internal error");
    }
}