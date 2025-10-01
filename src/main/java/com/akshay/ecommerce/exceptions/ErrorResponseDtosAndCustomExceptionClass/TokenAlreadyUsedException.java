package com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass;

public class TokenAlreadyUsedException extends RuntimeException {
    public TokenAlreadyUsedException(String message) {
        super(message);
    }

}