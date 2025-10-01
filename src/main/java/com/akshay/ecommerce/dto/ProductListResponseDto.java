package com.akshay.ecommerce.dto;


import java.util.UUID;

public record ProductListResponseDto(
        String id,
        String name,
        String description,
        String brand,
        boolean isCancelable,
        boolean isReturnable,
        boolean isActive,
        UUID categoryId,
        String categoryName,
        int variationCount
) {}