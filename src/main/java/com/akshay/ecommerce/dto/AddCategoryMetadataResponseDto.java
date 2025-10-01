package com.akshay.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCategoryMetadataResponseDto {

    private boolean success;
    private String message;
    private List<String> validationErrors;

    // Custom constructor for success response without validation errors
    public AddCategoryMetadataResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
