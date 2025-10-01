package com.akshay.ecommerce.dto.UpdateProductDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateProductRequestDto(
        @NotBlank(message = "Product name is required")
        @Size(min = 2, max = 50, message = "Product name should be between 2 and 50 characters")
        @Pattern(regexp = "^[A-Za-z ]+$", message = "Only Characters and Space Allowed")
        String name,
        @Size(min =2 ,max = 200, message = "Description should be between 2 and 200 characters")
        String description,
        @NotNull(message = "isCancellable flag is mandatory")
        Boolean isCancellable,
        @NotNull(message = "isReturnable flag is mandatory")
        Boolean isReturnable
) {}
