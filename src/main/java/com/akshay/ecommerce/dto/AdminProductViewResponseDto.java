package com.akshay.ecommerce.dto;

import com.akshay.ecommerce.dto.CustomerProductDtos.CategoryDetailsDto;

import java.util.List;

public record AdminProductViewResponseDto(
        String productId,
        String name,
        String description,
        String brand,
        boolean isCancelable,
        boolean isReturnable,
        boolean isActive,  // ✅ Admin-specific field
        CategoryDetailsDto category,
        List<AdminProductVariationDto> variations
) {}

