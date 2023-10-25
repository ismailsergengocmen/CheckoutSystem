package com.example.checkoutCase.exception;

public class ItemNotFoundException extends RuntimeException {

    public ItemNotFoundException(String message){
        super(message);
    }

}
