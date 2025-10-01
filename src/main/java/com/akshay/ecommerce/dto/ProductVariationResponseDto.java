package com.akshay.ecommerce.dto;

import java.util.Map;
import java.util.UUID;

public record ProductVariationResponseDto(
        UUID id,
        UUID productId,
        Map<String, Object> metadata,
        String imageName,
        Long quantityAvailable,
        Integer price,
        String productName,
        String productDescription,
        String categoryName,
        String sellerEmail
) {}