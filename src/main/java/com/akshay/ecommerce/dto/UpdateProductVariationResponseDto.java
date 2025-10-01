package com.akshay.ecommerce.dto;
public record UpdateProductVariationResponseDto(
        String message,
        String productVariationId,
        String imageUrl,
        Float price,
        Long quantityAvailable,
        Boolean isActive,
        boolean success
) {}