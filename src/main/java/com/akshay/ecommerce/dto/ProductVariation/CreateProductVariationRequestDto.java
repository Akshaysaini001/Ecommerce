package com.akshay.ecommerce.dto.ProductVariation;
//import com.akshay.ecommerce.config.MetadataValueNotNull;
//import jakarta.validation.constraints.*;
//import org.springframework.web.multipart.MultipartFile;
//import java.util.Map;
//import java.util.UUID;
//public record CreateProductVariationRequestDto(
//        @NotBlank(message = "Product ID is required")
//        @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$")
//        String productId,
//        @NotNull @NotEmpty @Size(min = 1, max = 20) @MetadataValueNotNull
//        Map<String, Object> metadata,
//        @NotNull @Min(0) @Max(999999) @Digits(integer = 6, fraction = 2)
//        Integer price,
//        @NotNull @Min(0) @Max(1000000)
//        Long quantityAvailable,
//        MultipartFile imageFile
//) {}
//

import com.akshay.ecommerce.config.MetadataValueNotNull;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@Getter
@Setter
public class CreateProductVariationRequestDto {
    @NotBlank(message = "Product ID is required")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[1-5][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$",
            message = "Invalid UUID format for productId")
    private String productId;

    @NotNull(message = "Metadata cannot be null")
    @Size(min = 1, max = 20, message = "Metadata must have between 1 and 20 entries")
    @MetadataValueNotNull
    private Map<String, Object> metadata;

    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be at least 1")
    @Max(value = 999999, message = "Price must be at most 999999")
    private Integer price;

    @NotNull(message = "Quantity available is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 1000000, message = "Quantity must be at most 1000000")
    private Long quantityAvailable;

    private MultipartFile imageFile;
}
