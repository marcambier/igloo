package com.igloo.inventory.controller;

import static com.igloo.inventory.exception.InventoryServiceException.INSUFFICIENT_STOCK;
import static com.igloo.inventory.exception.InventoryServiceException.UNKNOWN_INVENTORY;

import com.igloo.inventory.exception.InventoryServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class InventoryServiceExceptionHandler {

    @ExceptionHandler(InventoryServiceException.class)
    public ResponseEntity<String> handleBusinessException(InventoryServiceException ex) {

        HttpStatus status = switch (ex.getCode()) {
            case INSUFFICIENT_STOCK -> HttpStatus.CONFLICT;
            case UNKNOWN_INVENTORY -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        // FIXME : a better error handling would return a json object, not a String
        return new ResponseEntity<>(ex.getMessage(), status);
    }
}

