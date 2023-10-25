package com.example.checkoutCase.exception;

public class ItemCountLimitException extends RuntimeException{
    public ItemCountLimitException(String message){
        super(message);
    }
}
