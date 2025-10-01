package com.akshay.ecommerce.dto;
import java.util.Map;
import java.util.UUID;
public record ProductVariationViewResponseDto(
        UUID id,
        UUID productId,
        Map<String, Object> metadata,
        Long quantityAvailable,
        Float price,
        String imageUrl,
        String productName,
        String productDescription,
        String categoryName,
        String sellerEmail
) {}