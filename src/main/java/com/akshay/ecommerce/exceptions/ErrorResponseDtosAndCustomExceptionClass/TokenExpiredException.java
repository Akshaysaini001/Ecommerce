package com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }
}