package com.akshay.ecommerce.dto.UpdateCategory;
import java.util.List;
public record CategoryUpdateResponseDto(
        boolean success,
        String message,
        List<String> validationErrors
) {
    public CategoryUpdateResponseDto(boolean success, String message) {
        this(success, message, List.of());
    }
}