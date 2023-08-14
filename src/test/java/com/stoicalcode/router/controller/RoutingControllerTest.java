package com.stoicalcode.router.controller;

import com.stoicalcode.router.model.RouteResponseDto;
import com.stoicalcode.router.service.RoutingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void shouldReturnValidRoute() throws IOException {
        List<String> route = Arrays.asList("CZE", "AUT", "ITA");
        when(mockRoutingService.findLandRoute("CZE", "ITA")).thenReturn(route);

        ResponseEntity<RouteResponseDto> response = sut.findLandRoute("CZE", "ITA");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(route, response.getBody().getRoute());
        verify(mockRoutingService, times(1)).findLandRoute("CZE", "ITA");
        verifyNoMoreInteractions(mockRoutingService);
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidRouteArgumentsProvider.class)
    void shouldReturnBadRequestForInvalidRoute(String origin, String destination, List<String> expected) throws IOException {
        when(mockRoutingService.findLandRoute(origin, destination)).thenReturn(expected);

        ResponseEntity<RouteResponseDto> response = sut.findLandRoute(origin, destination);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(mockRoutingService, times(1)).findLandRoute(origin, destination);
        verifyNoMoreInteractions(mockRoutingService);
    }

    static class InvalidRouteArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("INVALID_ORIG_1", "INVALID_DEST_1", null),
                    Arguments.of("INVALID_ORIG_2", "INVALID_DEST_2", new ArrayList<>())
            );
        }
    }
}