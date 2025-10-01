package com.akshay.ecommerce.dto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class MetadataFieldDto {
    @NotEmpty(message = "cant be empty and FieldName is fixed you can only add values")
    @Size(min = 2, max = 50, message = "Size should be between 2 and 50")
    @Pattern(regexp = "^[a-zA-Z]*$", message = "It should only contain letters no spaces or other Allowed ")
    private String fieldName;
}
