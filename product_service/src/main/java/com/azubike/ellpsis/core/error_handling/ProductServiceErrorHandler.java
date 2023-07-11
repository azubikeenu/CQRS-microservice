package com.azubike.ellpsis.core.error_handling;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ProductServiceErrorHandler {


    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Object> handleIllegalStateException(IllegalArgumentException ex , WebRequest request){
        return new ResponseEntity<>(ex.getMessage() , new HttpHeaders() , HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Object> handleException(Exception ex , WebRequest request){
        return new ResponseEntity<>(ex.getMessage() , new HttpHeaders() , HttpStatus.BAD_REQUEST);
    }
}
