package com.akshay.ecommerce.dto;

import com.akshay.ecommerce.dto.viewCategory.AssociatedMetadataDto;
import com.akshay.ecommerce.entity.CategoryMetadataFieldValues;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryFilterDto {
    private List<AssociatedMetadataDto> metadataFields;
    private List<String> brands;
    private Float minPrice;
    private Float maxPrice;
}
