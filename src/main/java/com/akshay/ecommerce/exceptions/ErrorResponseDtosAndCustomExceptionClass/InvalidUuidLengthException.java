package com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass;


public class InvalidUuidLengthException extends RuntimeException {
    public InvalidUuidLengthException(String message)
    {
        super(message);
    }
}