package com.akshay.ecommerce.dto.UpdateProductDto;

public record UpdateProductResponseDto(
        String message,
        String productId,
        String productName,
        boolean success
) {}
