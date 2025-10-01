package com.akshay.ecommerce.dto.CustomerProductDtos;

import java.util.List;

public record CustomerProductSummaryDto(
        String productId,
        String name,
        String description,
        String brand,
        boolean isCancelable,
        boolean isReturnable,
        Float minPrice,
        Float maxPrice,
        List<String> variationImages,  // ✅ All variation images
        int totalVariations,
        boolean isActive
) {}
