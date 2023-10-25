package com.example.checkoutCase.exception;

public class IncompatibleItemTypesException extends RuntimeException {
    public IncompatibleItemTypesException(String message) {
        super(message);
    }
}
