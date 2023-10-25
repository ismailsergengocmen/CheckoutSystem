package com.example.checkoutCase.exception;

public class ExceededPriceException extends RuntimeException {
    public ExceededPriceException(String message){
        super(message);
    }
}
