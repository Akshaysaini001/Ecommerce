package com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass;


public class PermissionDenied extends RuntimeException {
    public PermissionDenied(String message) {
        super(message);
    }
}