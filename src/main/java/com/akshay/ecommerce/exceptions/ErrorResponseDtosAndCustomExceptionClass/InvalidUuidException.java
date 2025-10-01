package com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass;

public class InvalidUuidException extends RuntimeException {
    public InvalidUuidException(String message) {
        super(message);
    }
}