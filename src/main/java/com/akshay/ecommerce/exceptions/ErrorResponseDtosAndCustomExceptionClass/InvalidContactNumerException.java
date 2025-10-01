package com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass;

public class InvalidContactNumerException extends RuntimeException {
    public InvalidContactNumerException(String message) {
        super(message);
    }
}
