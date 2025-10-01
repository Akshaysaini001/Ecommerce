package com.akshay.ecommerce.dto.viewCategory;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.util.UUID;

@Data
public class CategoryBriefDto {
    private UUID id;
    private String name;
}