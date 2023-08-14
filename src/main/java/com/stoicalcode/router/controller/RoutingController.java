package com.stoicalcode.router.controller;

import com.stoicalcode.router.exception.InvalidCountryException;
import com.stoicalcode.router.exception.PathNotFoundException;
import com.stoicalcode.router.model.RouteResponseDto;
import com.stoicalcode.router.service.RoutingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
public class RoutingController {

    private final RoutingService routingService;

    @Autowired
    public RoutingController(RoutingService routingService) {
        this.routingService = routingService;
    }

    @GetMapping("/routing/{origin}/{destination}")
    public ResponseEntity<?> findLandRoute(@PathVariable String origin, @PathVariable String destination) {
        try {
            List<String> route = routingService.findLandRoute(origin, destination);
            return ResponseEntity.ok(new RouteResponseDto(route));

        } catch (InvalidCountryException | PathNotFoundException e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            log.error("Internal error occurred: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}