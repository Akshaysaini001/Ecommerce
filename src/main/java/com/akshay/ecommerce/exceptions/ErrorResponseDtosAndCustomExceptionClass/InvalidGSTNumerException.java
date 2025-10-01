package com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass;

public class InvalidGSTNumerException extends RuntimeException {
    public InvalidGSTNumerException(String message) {
        super(message);
    }
}