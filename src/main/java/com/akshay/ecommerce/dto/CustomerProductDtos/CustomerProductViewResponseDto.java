package com.akshay.ecommerce.dto.CustomerProductDtos;

import java.util.List;

public record CustomerProductViewResponseDto(
        String productId,
        String name,
        String description,
        String brand,
        boolean isCancelable,
        boolean isReturnable,
        CategoryDetailsDto category,
        List<CustomerProductVariationDto> variations
) {}