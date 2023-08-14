package com.stoicalcode.router.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCountryException extends IllegalArgumentException {

    public InvalidCountryException(String message) {
        super(message);
    }
}