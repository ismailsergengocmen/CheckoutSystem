package com.example.checkoutCase.exception;

public class UniqueItemCountLimit extends RuntimeException {
    public UniqueItemCountLimit(String message) {
        super(message);
    }
}
