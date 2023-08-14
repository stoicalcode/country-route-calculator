package com.stoicalcode.router.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PathNotFoundException extends RuntimeException {

    public PathNotFoundException(String message) {
        super(message);
    }
}