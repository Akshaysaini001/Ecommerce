package com.akshay.ecommerce.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCategoryMetadataRequestDto {
    @NotNull(message = "Category ID is required")
    private String categoryId;

    @NotNull(message = "Metadata Field ID is required")
    private String metadataFieldId;

    @NotNull(message = "Values are required")
    private List<String> values;
}
