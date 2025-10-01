package com.akshay.ecommerce.dto.viewCategory;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.util.List;
import java.util.UUID;
@Data
public class CategoryDetailDto {

private UUID id;
    private String name;
    private List<CategoryBriefDto> parentPath;
    private List<CategoryBriefDto> children;
    private List<AssociatedMetadataDto> associatedMetadata;
}