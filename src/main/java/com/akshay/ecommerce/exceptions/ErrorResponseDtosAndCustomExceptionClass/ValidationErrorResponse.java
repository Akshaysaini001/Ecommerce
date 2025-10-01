package com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidationErrorResponse {
    private String message;
    private List<String> errors;
}