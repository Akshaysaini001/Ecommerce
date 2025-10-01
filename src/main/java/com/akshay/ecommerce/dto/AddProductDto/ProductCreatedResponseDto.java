package com.akshay.ecommerce.dto.AddProductDto;
import java.util.UUID;
public record ProductCreatedResponseDto(
        UUID id,
        String name,
        String description,
        String brand,
        boolean isCancelable,
        boolean isReturnable,
        boolean isActive
) {}