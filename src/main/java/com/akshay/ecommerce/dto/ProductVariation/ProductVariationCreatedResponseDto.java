package com.akshay.ecommerce.dto.ProductVariation;


import com.fasterxml.jackson.databind.JsonNode;

import java.util.UUID;

public record ProductVariationCreatedResponseDto(
        UUID id,
        UUID productId,
        JsonNode metadata,
        String imageName,
        Long quantityAvailable,
        Float price,
        String message
) {}
