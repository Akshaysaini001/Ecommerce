package com.akshay.ecommerce.dto;

import java.util.UUID;

public record ViewProductDto(
        String productId,
        String name,
        String description,
        String brand,
        boolean isCancelable,
        boolean isReturnable,
        boolean isActive,
        UUID categoryId,
        String categoryName,
        String sellerEmail
) {}