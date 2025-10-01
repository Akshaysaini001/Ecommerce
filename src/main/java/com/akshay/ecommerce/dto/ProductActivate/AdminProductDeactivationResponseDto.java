package com.akshay.ecommerce.dto.ProductActivate;

public record AdminProductDeactivationResponseDto(
        String message,
        String productId,
        String productName,
        boolean success,
        String sellerEmail
) {}