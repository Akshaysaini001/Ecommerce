package com.akshay.ecommerce.dto;

public record DeleteProductResponseDto(
        String message,
        String productId,
        String productName,
        boolean success
) {}
