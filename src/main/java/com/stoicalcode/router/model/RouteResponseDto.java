package com.stoicalcode.router.model;

import lombok.Getter;

import java.util.List;

@Getter
public class RouteResponseDto {
    private List<String> route;

    public RouteResponseDto(List<String> route) {
        this.route = route;
    }
}