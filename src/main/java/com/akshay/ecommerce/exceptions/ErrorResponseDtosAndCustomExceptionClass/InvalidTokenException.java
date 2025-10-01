package com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }
}