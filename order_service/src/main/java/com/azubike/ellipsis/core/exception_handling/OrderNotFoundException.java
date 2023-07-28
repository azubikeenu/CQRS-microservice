package com.azubike.ellipsis.core.exception_handling;

public class OrderNotFoundException extends RuntimeException{
    public OrderNotFoundException(final String message) {
        super(message);
    }
}
