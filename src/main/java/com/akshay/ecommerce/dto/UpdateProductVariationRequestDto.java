package com.akshay.ecommerce.dto;
import com.akshay.ecommerce.config.MetadataValueNotNull;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
//public record UpdateProductVariationRequestDto(
//        @Positive(message = "Price must be positive")
//        @Max(value = 1000000, message = "Price cannot exceed 1,000,000")
//        Float price,
//
//        @Min(value = 0, message = "Quantity should be zero or more")
//        @Max(value = 1000000, message = "Quantity cannot exceed 1,000,000 units")
//        Long quantityAvailable,
//
//        @Size(min = 1, max = 20, message = "Metadata must have 1-20 fields")
//        @MetadataValueNotNull
//        Map<String, Object> metadata,
//
//        Boolean isActive,
//        MultipartFile imageFile
//) {}





