package com.akshay.ecommerce.dto.AddProductDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;
public record CreateProductRequestDto(

        @NotBlank(message = "Product name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        @Pattern(regexp = "^[A-Za-z0-9\\s\\-\\.\\(\\)]+$", message = "Name can contain alphabets, numbers, spaces, hyphens, dots and brackets")
        String name,

        @Size( min=2 , max = 500, message = "Description cannot exceed 500 characters and minimum 2 ")
        @NotBlank(message = "Description is required")
        @Pattern(regexp = "^[A-Za-z0-9\\s]*$", message = "Description can contain only alphabets, numbers and spaces")
        String description,

        @NotBlank(message = "Brand is required")
        @Size(min = 1, max = 50, message = "Brand must be between 1 and 50 characters")
        @Pattern(regexp = "^[A-Za-z0-9\\s\\-\\.&]+$", message = "Brand can contain alphabets, numbers, spaces, hyphens, dots and ampersand")
        String brand,


        @NotBlank(message = "Category ID is required")
        @Pattern(
                regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
                message = "Category ID must be a valid UUID format"
        )
        String categoryId,

        @NotNull(message = "Cancelable status is required")
        Boolean isCancelable,

        @NotNull(message = "Returnable status is required")
        Boolean isReturnable
) {}
