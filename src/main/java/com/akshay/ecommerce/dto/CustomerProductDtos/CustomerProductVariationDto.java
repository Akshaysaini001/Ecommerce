package com.akshay.ecommerce.dto.CustomerProductDtos;

import java.util.Map;

public record CustomerProductVariationDto(
        String variationId,
        Map<String, Object> metadata,
        Float price,
        Long quantityAvailable,
        String imageUrl
) {}