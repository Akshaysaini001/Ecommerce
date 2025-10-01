package com.akshay.ecommerce.dto.viewCategory;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssociatedMetadataDto {
    private UUID fieldId;
    private String fieldName;
    private List<String> possibleValues;
}