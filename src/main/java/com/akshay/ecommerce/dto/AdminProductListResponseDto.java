package com.akshay.ecommerce.dto;

import java.util.List;

public record AdminProductListResponseDto(
        List<AdminProductSummaryDto> products
) {}