package com.akshay.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
public record ChangePasswordRequestDto(
        @NotBlank
        @Size(min = 8, max = 15)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,15}$",
                message = "Password must be 8-15 chars with at least 1 uppercase, 1 lowercase, 1 digit, and 1 special character (@#$%^&+=!)")
        String password,
        @NotBlank
        String confirmPassword
) {}