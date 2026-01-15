package com.igloo.inventory.exception;

import lombok.Getter;

@Getter
public class InventoryServiceException extends Exception {

    public static final String INSUFFICIENT_STOCK = "INSUFFICIENT_STOCK";
    public static final String UNKNOWN_INVENTORY = "UNKNOWN_INVENTORY";
    // TODO : decline all functional error cases here

    private final String code;

    public InventoryServiceException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
