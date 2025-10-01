package com.akshay.ecommerce.dto;

import com.fasterxml.jackson.databind.JsonNode;

public record ProductVariationListResponseDto(
        String id,
        Integer quantityAvailable,
        Float price,
        JsonNode metadata,
        String imageName,
        boolean isActive
) {}
