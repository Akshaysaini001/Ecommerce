package com.akshay.ecommerce.dto;

import java.util.Map;

public record AdminProductVariationDto(
        String variationId,
        Map<String, Object> metadata,
        Float price,
        Long quantityAvailable,
        String imageUrl,
        boolean isActive  // ✅ Admin can see variation status
) {}