package com.stoicalcode.router.controller;

import com.stoicalcode.router.exception.InvalidCountryException;
import com.stoicalcode.router.exception.PathNotFoundException;
import com.stoicalcode.router.model.RouteResponseDto;
import com.stoicalcode.router.service.RoutingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
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
    public ResponseEntity<RouteResponseDto> findLandRoute(@PathVariable String origin, @PathVariable String destination) {
        try {
            List<String> route = routingService.findLandRoute(origin, destination);
            if (CollectionUtils.isEmpty(route)){
                return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok(new RouteResponseDto(route));

        } catch (IOException e) {
            log.error("Error occurred while finding land route from '{}' to '{}'", origin, destination, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}