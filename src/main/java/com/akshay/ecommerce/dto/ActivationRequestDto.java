package com.akshay.ecommerce.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

public record ActivationRequestDto(
        @NotBlank(message = "token is required")
        String token
){}
