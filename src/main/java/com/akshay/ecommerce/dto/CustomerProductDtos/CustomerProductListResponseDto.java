package com.akshay.ecommerce.dto.CustomerProductDtos;
import java.util.List;
public record CustomerProductListResponseDto(
        List<CustomerProductSummaryDto> products,
        CategoryDetailsDto category
) {}