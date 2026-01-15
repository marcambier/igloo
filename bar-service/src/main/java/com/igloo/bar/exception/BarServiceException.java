package com.igloo.bar.exception;

import lombok.Getter;

@Getter
public class BarServiceException extends Exception {

    public static final String INSUFFICIENT_STOCK = "INSUFFICIENT_STOCK";
    // TODO : decline all functional error cases here

    private final String code;

    public BarServiceException(String code, String message) {
        super(message);
        this.code = code;
    }
}
