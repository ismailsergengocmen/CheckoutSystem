package com.example.checkoutCase.exception;

public class ExceededSizeException extends RuntimeException {
    public ExceededSizeException(String message){
        super(message);
    }
}
