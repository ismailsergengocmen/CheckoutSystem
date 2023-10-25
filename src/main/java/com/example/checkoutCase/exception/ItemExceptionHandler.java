package com.example.checkoutCase.exception;

import com.example.checkoutCase.entity.RequestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ItemExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<RequestResponse> handleException(ItemNotFoundException exc) {

        RequestResponse requestResponse = new RequestResponse(false,exc.getMessage());

        return new ResponseEntity<>(requestResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<RequestResponse> handleException(InvalidArgumentException exc) {
        RequestResponse requestResponse = new RequestResponse(false,exc.getMessage());

        return new ResponseEntity<>(requestResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<RequestResponse> handleException(ExceededSizeException exc) {
        RequestResponse requestResponse = new RequestResponse(false,exc.getMessage());

        return new ResponseEntity<>(requestResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<RequestResponse> handleException(IncompatibleItemTypesException exc) {
        RequestResponse requestResponse = new RequestResponse(false,exc.getMessage());

        return new ResponseEntity<>(requestResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<RequestResponse> handleException(ItemCountLimitException exc) {
        RequestResponse requestResponse = new RequestResponse(false,exc.getMessage());

        return new ResponseEntity<>(requestResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<RequestResponse> handleException(ExceededPriceException exc) {
        RequestResponse requestResponse = new RequestResponse(false,exc.getMessage());

        return new ResponseEntity<>(requestResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<RequestResponse> handleException(UniqueItemCountLimit exc) {
        RequestResponse requestResponse = new RequestResponse(false,exc.getMessage());

        return new ResponseEntity<>(requestResponse, HttpStatus.BAD_REQUEST);
    }

//    // Generic exception handler
//    @ExceptionHandler
//    public ResponseEntity<ItemErrorResponse> handleException(Exception exc){
//        ItemErrorResponse error = new ItemErrorResponse();
//
//        error.setStatus(HttpStatus.BAD_REQUEST.value());
//        error.setMessage(exc.getMessage());
//
//        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//    }
}
