package com.akshay.ecommerce.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.UUID;

@Data
public class AddCategoryDto {
    @NotBlank
    @NotEmpty(message = "Category Name cannot be empty.")
    @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z ]*$",message = "Only letters allowed and also Spaces")
    private String categoryName;
    private String  parentId;
}