package com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass;

public class RequiredFieldMissingException extends RuntimeException {
    public RequiredFieldMissingException(String message) {
        super(message);
    }
}