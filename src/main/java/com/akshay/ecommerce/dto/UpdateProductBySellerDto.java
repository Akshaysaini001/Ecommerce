package com.akshay.ecommerce.dto;

import com.akshay.ecommerce.config.MetadataValueNotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Getter
@Setter
public class UpdateProductBySellerDto {
    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be at least 1")
    @Max(value = 999999, message = "Price must be at most 999999")
    private Float price;

    @NotNull(message = "Quantity available is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 1000000, message = "Quantity must be at most 1000000")
    private Long quantityAvailable;

    @NotNull(message = "Metadata cannot be null")
    @Size(min = 1, max = 20, message = "Metadata must have between 1 and 20 entries")
    @MetadataValueNotNull
    private Map<String, Object> metadata;

    @NotNull(message = "Active status is required")
    private Boolean isActive;

    private MultipartFile imageFile;
}
