package com.akshay.ecommerce.dto.UpdateCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.UUID;
public record CategoryUpdateRequestDto(
        @NotNull(message = "Category ID is required")
        String id,
        @NotBlank(message = "Category name is required")
        @Size(max = 255, message = "Category name cannot exceed 255 characters")
        @Pattern(regexp = "^[a-zA-Z ]*$",message = "Only letters allowed and also Spaces")
        String name
) {}