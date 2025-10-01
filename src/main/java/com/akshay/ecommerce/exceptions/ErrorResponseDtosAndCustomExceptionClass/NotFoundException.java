package com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
