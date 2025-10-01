package com.akshay.ecommerce.dto;

import com.akshay.ecommerce.dto.CustomerProductDtos.CategoryDetailsDto;

import java.util.List;

public record AdminProductSummaryDto(
        String productId,
        String name,
        String description,
        String brand,
        boolean isCancelable,
        boolean isReturnable,
        Float minPrice,
        Float maxPrice,
        List<String> variationImages,
        int totalVariations,
        int activeVariations,        // ✅ Admin-specific
        boolean isActive,            // ✅ Admin-specific
        CategoryDetailsDto category, // ✅ Admin-specific
        SellerDetailsDto seller      // ✅ Admin-specific
) {}


