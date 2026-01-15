package com.igloo.bar.controller;

import static com.igloo.bar.exception.BarServiceException.INSUFFICIENT_STOCK;

import com.igloo.bar.exception.BarServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BarServiceExceptionHandler {

    @ExceptionHandler(BarServiceException.class)
    public ResponseEntity<String> handleBusinessException(BarServiceException ex) {

        HttpStatus status = switch (ex.getCode()) {
            case INSUFFICIENT_STOCK -> HttpStatus.CONFLICT;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        // FIXME : a better error handling would return a json object, not a String
        return new ResponseEntity<>(ex.getMessage(), status);
    }
}

