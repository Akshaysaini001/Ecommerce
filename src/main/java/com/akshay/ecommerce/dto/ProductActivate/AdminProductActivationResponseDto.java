package com.akshay.ecommerce.dto.ProductActivate;

public record AdminProductActivationResponseDto(
        String message,
        String productId,
        String productName,
        boolean success,
        String sellerEmail
) {}