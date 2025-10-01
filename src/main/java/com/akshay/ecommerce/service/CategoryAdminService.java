package com.akshay.ecommerce.service;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.InvalidUuidException;
import com.akshay.ecommerce.repository.CategoryMetadataFieldRepository;
import com.akshay.ecommerce.repository.CategoryMetadataFieldValuesRepository;
import com.akshay.ecommerce.repository.CategoryRepository;
import com.akshay.ecommerce.repository.ProductRepository;
import com.akshay.ecommerce.dto.*;
import com.akshay.ecommerce.dto.UpdateCategory.CategoryUpdateRequestDto;
import com.akshay.ecommerce.dto.UpdateCategory.CategoryUpdateResponseDto;
import com.akshay.ecommerce.dto.viewCategory.AssociatedMetadataDto;
import com.akshay.ecommerce.dto.viewCategory.CategoryBriefDto;
import com.akshay.ecommerce.dto.viewCategory.CategoryDetailDto;
import com.akshay.ecommerce.entity.Category;
import com.akshay.ecommerce.entity.CategoryMetadataField;
import com.akshay.ecommerce.entity.CategoryMetadataFieldValues;
import com.akshay.ecommerce.exceptions.DuplicateException;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.NotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryAdminService {
    private final MessageSource messageSource;
    private final CategoryMetadataFieldRepository categoryMetadataFieldRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final CategoryMetadataFieldRepository fieldRepository;
    private final CategoryMetadataFieldValuesRepository valuesRepository;

    @Transactional
    public UUID addMetadataField(MetadataFieldDto metadataFieldDto) {
        categoryMetadataFieldRepository.findByNameIgnoreCase(metadataFieldDto.getFieldName())
                .ifPresent(e -> {
                    throw new DuplicateException("This is duplicate");
                });

        CategoryMetadataField Field = new CategoryMetadataField();
        Field.setName(metadataFieldDto.getFieldName());
        CategoryMetadataField saving = categoryMetadataFieldRepository.save(Field);
        return saving.getId();
    }

    @Transactional
    public Page<MetadataFieldViewDto> getAllMetadataFields(int pageOffset, int pageSize, String sortField, String sortOrder) {
        Sort.Direction direction = Sort.Direction.ASC;
        if (sortOrder.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = PageRequest.of(pageOffset, pageSize, Sort.by(direction, sortField));
        Page<CategoryMetadataField> fieldsPage;
        fieldsPage = categoryMetadataFieldRepository.findAll(pageable);
        return fieldsPage.map(field -> {
            MetadataFieldViewDto dto = new MetadataFieldViewDto();
            dto.setId(field.getId());
            dto.setName(field.getName());
            return dto;
        });
    }

    @Transactional
    public UUID addCategory(AddCategoryDto addCategoryDto) {
        UUID parentUuid = null;
        if (addCategoryDto.getParentId() != null) {
            try {
                parentUuid = UUID.fromString(addCategoryDto.getParentId());
            } catch (IllegalArgumentException e) {
                throw new InvalidUuidException("Invalid UUID format: " + addCategoryDto.getParentId());
            }
        }

        Category parent = null;
        if (parentUuid != null) {
            parent = categoryRepository.findById(parentUuid)
                    .orElseThrow(() -> new NotFoundException("Parent category not found."));
        }
        //check this parent ka already exist krta hai ye category or not
        categoryRepository.findByNameAndParent(addCategoryDto.getCategoryName(), parent)
                .ifPresent(existingCategory -> {
                    throw new DuplicateException("A category with the name '" + addCategoryDto.getCategoryName() + "' already exists under this parent");
                });

        if (productRepository.existsByCategory(parent)) {
            throw new ValidationException("Cannot create a sub-category under a category that is already associated with products");
        }

        Category current = parent;
        while (current != null) {
            if (current.getName().equalsIgnoreCase(addCategoryDto.getCategoryName())) {
                throw new DuplicateException("A category with the name '" + addCategoryDto.getCategoryName() + "' already exists in the parent hierarchy");
            }
            current = current.getParent();
        }
        Category newCategory = new Category();
        newCategory.setName(addCategoryDto.getCategoryName());
        newCategory.setParent(parent);
        return categoryRepository.save(newCategory).getId();
    }//find parent , do horizontal than vertical check unique and if tere is a product added at a category
    //we cant add other sub category in it


    @Transactional
    public CategoryDetailDto getCategoryDetails(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with ID " + categoryId + " not found"));

        CategoryDetailDto dto = new CategoryDetailDto();
        dto.setId(category.getId());
        dto.setName(category.getName());

        List<CategoryBriefDto> parentPath = new ArrayList<>();
        Category currentParent = category.getParent();
        while (currentParent != null) {
            CategoryBriefDto parentDto = new CategoryBriefDto();
            parentDto.setId(currentParent.getId());
            parentDto.setName(currentParent.getName());
            parentPath.add(parentDto);
            currentParent = currentParent.getParent();
        }
        Collections.reverse(parentPath);
        dto.setParentPath(parentPath);

        List<CategoryBriefDto> children = new ArrayList<>();
        for (Category child : category.getChildren()) {
            CategoryBriefDto childDto = new CategoryBriefDto();
            childDto.setId(child.getId());
            childDto.setName(child.getName());
            children.add(childDto);
        }
        dto.setChildren(children);
        List<AssociatedMetadataDto> associatedMetadata = new ArrayList<>();
        for (CategoryMetadataFieldValues fieldValue : category.getMetadataFieldValues()) {
            AssociatedMetadataDto metadataDto = new AssociatedMetadataDto();
            CategoryMetadataField metadataDefinition = fieldValue.getCategoryMetadataField();

            metadataDto.setFieldId(metadataDefinition.getId());
            metadataDto.setFieldName(metadataDefinition.getName());
            metadataDto.setPossibleValues(fieldValue.getValues());

            associatedMetadata.add(metadataDto);
        }
        dto.setAssociatedMetadata(associatedMetadata);

        return dto;
    }


    //  check if that id exis or not if then give its id and name of category or whatever itis
    // than next field is to set the value of parentpath until we found and add it in dto , than find the children only one immidiate
    //and show the metadata fields

    //
    @Transactional
    public List<CategoryDetailDto> getAllCategoriesWithDetails(
            int max, int offset, String sort, String order, String query) {
        Pageable pageable = PageRequest.of(offset / max, max, order.equals("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending()
        );
        Page<Category> categoryPage;
        if (query != null && !query.trim().isEmpty()) {
            categoryPage = categoryRepository.findByNameContainingIgnoreCase(query, pageable);
        } else {
            categoryPage = categoryRepository.findAll(pageable);
        }
        List<CategoryDetailDto> categoryDetailsList = new ArrayList<>();
        for (Category category : categoryPage.getContent()) {
            CategoryDetailDto dto = new CategoryDetailDto();
            dto.setId(category.getId());
            dto.setName(category.getName());
            List<CategoryBriefDto> parentPath = new ArrayList<>();
            Category currentParent = category.getParent();
            while (currentParent != null) {
                CategoryBriefDto parentDto = new CategoryBriefDto();
                parentDto.setId(currentParent.getId());
                parentDto.setName(currentParent.getName());
                parentPath.add(parentDto);
                currentParent = currentParent.getParent();
            }
            Collections.reverse(parentPath);
            dto.setParentPath(parentPath);
            dto.setChildren(
                    category.getChildren().stream().map(child -> {
                        CategoryBriefDto childDto = new CategoryBriefDto();
                        childDto.setId(child.getId());
                        childDto.setName(child.getName());
                        return childDto;
                    }).collect(Collectors.toList())
            );
            dto.setAssociatedMetadata(
                    category.getMetadataFieldValues().stream().map(fieldValue -> {
                        AssociatedMetadataDto metadataDto = new AssociatedMetadataDto();
                        CategoryMetadataField metadataDefinition = fieldValue.getCategoryMetadataField();
                        metadataDto.setFieldId(metadataDefinition.getId());
                        metadataDto.setFieldName(metadataDefinition.getName());
                        metadataDto.setPossibleValues(fieldValue.getValues());
                        return metadataDto;
                    }).collect(Collectors.toList())
            );
            categoryDetailsList.add(dto);
        }
        return categoryDetailsList;
    }

    @Transactional
    public CategoryUpdateResponseDto updateCategory(CategoryUpdateRequestDto requestDto) {
        UUID categoryId = UUID.fromString(requestDto.id());
        String newName = requestDto.name().trim();
        Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
        if (optionalCategory.isEmpty()) {
            return new CategoryUpdateResponseDto(
                    false,
                    "Validation failed",
                    List.of("Category with ID " + categoryId + " not found")
            );
        }
        Category existingCategory = optionalCategory.get();
        Category parent = existingCategory.getParent();
        String horizontalError = validateHorizontalUniqueness(newName, categoryId, parent);
        if (horizontalError != null) {
            return new CategoryUpdateResponseDto(false, "Validation failed", List.of(horizontalError));
        }
        String verticalError = validateVerticalUniqueness(newName, parent);
        if (verticalError != null) {
            return new CategoryUpdateResponseDto(false, "Validation failed", List.of(verticalError));
        }
        existingCategory.setName(newName);
        categoryRepository.save(existingCategory);
        return new CategoryUpdateResponseDto(true, "Category updated successfully");
    }

    private String validateHorizontalUniqueness(String newName, UUID categoryId, Category parent) {
        Optional<Category> existing = categoryRepository.findByNameAndParent(newName, parent);
        if (existing.isPresent() && !existing.get().getId().equals(categoryId)) {
            String level = parent != null ? "under parent '" + parent.getName() + "'" : "at root level";
            return "A category with the name" + newName + "already exists " + level;
        }
        return null;
    }
    private String validateVerticalUniqueness(String newName, Category parent) {
        Category current = parent;
        while (current != null) {
            if (current.getName().equalsIgnoreCase(newName)) {
                return "A category with the name '" + newName + "' already exists in the parent hierarchy";
            }
            current = current.getParent();
        }
        return null;
    }


    @Transactional
    public AddCategoryMetadataResponseDto addCategoryMetadata(AddCategoryMetadataRequestDto request) {
        List<String> errors = new ArrayList<>();
        UUID categoryId;
        try {
            categoryId = UUID.fromString(request.getCategoryId());
        } catch (IllegalArgumentException e) {
            errors.add("Category ID format is invalid");
            return new AddCategoryMetadataResponseDto(false, "Validation failed", errors);
        }
        UUID metadataFieldId;
        try {
            metadataFieldId = UUID.fromString(request.getMetadataFieldId());
        } catch (IllegalArgumentException e) {
            errors.add("Metadata Field ID format is invalid");
            return new AddCategoryMetadataResponseDto(false, "Validation failed", errors);
        }
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            errors.add("Category ID should be valid");
        }
        CategoryMetadataField field = fieldRepository.findById(metadataFieldId).orElse(null);
        if (field == null) {
            errors.add("Metadata Field ID should be valid");
        }
        if (request.getValues() == null || request.getValues().isEmpty()) {
            errors.add("There should be at least one value for each field");
        }
        if (request.getValues() != null && !request.getValues().isEmpty()) {
            Set<String> uniqueValues = new HashSet<>();
            boolean duplicateFound = false;
            for (String value : request.getValues()) {
                if (value == null || value.trim().isEmpty()) {
                    errors.add("Values cannot be empty or blank");
                    break;
                }
                String lower = value.trim().toLowerCase(); // normalize to lowercase
                if (!uniqueValues.add(lower)) {
                    duplicateFound = true;
                }
            }
            if (duplicateFound) {
                errors.add("Values should be unique case-insensitive");
            }
        }
        if (!errors.isEmpty()) {
            return new AddCategoryMetadataResponseDto(false, "Validation failed", errors);
        }
        boolean alreadyExists = valuesRepository.existsByCategoryAndCategoryMetadataField(category, field);
        if (alreadyExists) {
            errors.add("This metadata field already exists for the category");
            return new AddCategoryMetadataResponseDto(false, "Validation failed", errors);
        }
        CategoryMetadataFieldValues entity = new CategoryMetadataFieldValues();
        entity.setCategory(category);
        entity.setCategoryMetadataField(field);
        List<String> myValues = request.getValues().stream()
                .map(v -> v.trim())
                .toList();
        entity.setValues(myValues);
        valuesRepository.save(entity);
        return new AddCategoryMetadataResponseDto(true, "Category metadata added successfully");
    }

//flow for add and upate metadata fields
//
//    Validate IDs  list; confirm the category–field pair does not already exist.
//    Trim incoming values, build a new CategoryMetadataFieldValues entity, save – inserts a fresh row.
//    Validate IDs + list; fetch the existing category–field entity (must already exist).
//    Trim new values, overwrite setValues(...) on that entity, save –  updates the existing row.


    @Transactional
    public AddCategoryMetadataResponseDto updateCategoryMetadata(AddCategoryMetadataRequestDto request) {
        List<String> errors = new ArrayList<>();
        UUID categoryId;
        try {
            categoryId = UUID.fromString(request.getCategoryId());
        } catch (IllegalArgumentException e) {
            errors.add("Category ID format is invalid");
            return new AddCategoryMetadataResponseDto(false, "Validation failed", errors);
        }
        UUID metadataFieldId;
        try {
            metadataFieldId = UUID.fromString(request.getMetadataFieldId());
        } catch (IllegalArgumentException e) {
            errors.add("Metadata Field ID format is invalid");
            return new AddCategoryMetadataResponseDto(false, "Validation failed", errors);
        }
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            errors.add("Category ID should be valid");
        }
        CategoryMetadataField field = fieldRepository.findById(metadataFieldId).orElse(null);
        if (field == null) {
            errors.add("Metadata Field ID should be valid");
        }
        if (!errors.isEmpty()) {
            return new AddCategoryMetadataResponseDto(false, "Validation failed", errors);
        }
        CategoryMetadataFieldValues existingEntity =
                valuesRepository.findByCategoryAndCategoryMetadataField(category, field).orElse(null);
        if (existingEntity == null) {
            errors.add("Metadata Field must already be associated with the Category");
            return new AddCategoryMetadataResponseDto(false, "Validation failed", errors);
        }
        validateValues(request.getValues(), errors);
        if (!errors.isEmpty()) {
            return new AddCategoryMetadataResponseDto(false, "Validation failed", errors);
        }
        existingEntity.setValues(
                request.getValues().stream()
                        .map(v -> v.trim())
                        .collect(Collectors.toList())
        );
        valuesRepository.save(existingEntity);
        return new AddCategoryMetadataResponseDto(true, "Category metadata updated successfully");
    }

    private void validateValues(List<String> values, List<String> errors) {
        if (values == null || values.isEmpty()) {
            errors.add("There should be at least one value for each field");
            return;
        }
        Set<String> uniqueValues = new HashSet<>();
        for (String value : values) {
            if (value == null || value.trim().isEmpty()) {
                errors.add("Values cannot be empty or blank");
                continue;
            }
            String trimmed = value.trim();
            if (trimmed.length() < 2 || trimmed.length() > 50) {
                errors.add("Each value must be between 2 and 50 characters: " + trimmed);
            }
            String lower = trimmed.toLowerCase();
            if (!uniqueValues.add(lower)) {
                errors.add("Values should be unique case-insensitive: " + trimmed);
            }
        }
    }


}













