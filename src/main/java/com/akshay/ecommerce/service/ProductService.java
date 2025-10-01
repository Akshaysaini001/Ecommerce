package com.akshay.ecommerce.service;
import com.akshay.ecommerce.dto.UpdateProductDto.UpdateProductRequestDto;
import com.akshay.ecommerce.dto.UpdateProductDto.UpdateProductResponseDto;
import com.akshay.ecommerce.dto.*;
import com.akshay.ecommerce.dto.AddProductDto.CreateProductRequestDto;
import com.akshay.ecommerce.dto.AddProductDto.ProductCreatedResponseDto;
import com.akshay.ecommerce.dto.ProductVariation.CreateProductVariationRequestDto;
import com.akshay.ecommerce.dto.ProductVariation.ProductVariationCreatedResponseDto;
import com.akshay.ecommerce.entity.*;
import com.akshay.ecommerce.exceptions.DuplicateException;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.InvalidUuidException;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.NotFoundException;
import com.akshay.ecommerce.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;
    private final EmailService emailService;
    private final ProductVariationRepository productVariationRepository;
    private final S3ImageService s3ImageService;
    private final CategoryMetadataFieldValuesRepository categoryMetadataFieldValuesRepository;
    private final CategoryMetadataFieldRepository categoryFieldRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public ProductCreatedResponseDto createProduct(CreateProductRequestDto req, String sellerEmail) {
        Seller seller = sellerRepository.findByUserEmail(sellerEmail)
                .orElseThrow(() -> new NotFoundException("Seller not found"));
        UUID categoryUuid;
        try {
            categoryUuid = UUID.fromString(req.categoryId());
        } catch (IllegalArgumentException e) {
            throw new InvalidUuidException("Invalid Category ID format");
        }
        Category category = categoryRepository.findById(categoryUuid)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        if (!categoryRepository.isLeafCategory(categoryUuid)) {
            throw new InvalidUuidException("Category ID passed should be of valid leaf node category");
        }
        if (productRepository.existsByNameAndBrandAndCategoryAndSeller(req.name(), req.brand(), category, seller)) {
            throw new ValidationException("Product name should be unique with respect to brand, category and seller combination");
        }
        Product product = new Product();product.setName(req.name());
        product.setDescription(req.description());product.setBrand(req.brand());
        product.setCancelable(req.isCancelable());product.setReturnable(req.isReturnable());
        product.setCategory(category);product.setSeller(seller);
        product.setActive(false);product.setDeleted(false);
        Product saved = productRepository.save(product);
        emailService.sendProductAdminNotification(
                saved.getName(), saved.getBrand(), seller.getCompanyName()
        );
        return new ProductCreatedResponseDto(saved.getId(), saved.getName(), saved.getDescription(),
                saved.getBrand(), saved.isCancelable(), saved.isReturnable(), saved.isActive()
        );
    }
    //Same seller cannot add duplicate products with same name, brand, and category but diffrenet seller can sell same product


    @Value("${app.image.base-url}")
    private String defaultImage;
    @Transactional
    public ProductVariationCreatedResponseDto createProductVariation(CreateProductVariationRequestDto req, String sellerEmail) {
        UUID productUuid;
        try {
            productUuid = UUID.fromString(req.getProductId());
        } catch (IllegalArgumentException e) {
            throw new InvalidUuidException("Invalid Product ID format");
        }
        Product product = productRepository.findById(productUuid)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        if (!product.getSeller().getEmail().equals(sellerEmail)) {
            throw new ValidationException("You can add variations only to your own products");
        }
        if (!product.isActive()) {
            throw new ValidationException("Product should be active to add variations");
        }
        if (product.isDeleted()) {
            throw new ValidationException("Product should be non-deleted and active");
        }
        String imageUrl;
        try {
            if (req.getImageFile() != null && !req.getImageFile().isEmpty()) {
                s3ImageService.validateImage(req.getImageFile());
                imageUrl = s3ImageService.uploadProductVariationImage(req.getImageFile());
            } else {
                imageUrl = defaultImage;
            }
        } catch (IOException | IllegalArgumentException e) {
            throw new ValidationException("Image upload failed: " + e.getMessage());
        }
        JsonNode metadataNode = objectMapper.convertValue(req.getMetadata(), JsonNode.class);
        ProductVariation variation = new ProductVariation();
        variation.setProduct(product);
        variation.setQuantityAvailable(req.getQuantityAvailable());
        variation.setPrice(req.getPrice());
        variation.setMetadata(metadataNode);
        variation.setImageName(imageUrl);
        variation.setActive(true);
        ProductVariation saved = productVariationRepository.save(variation);
        return new ProductVariationCreatedResponseDto(
                saved.getId(),
                saved.getProduct().getId(),
                saved.getMetadata(),
                saved.getImageName(),
                saved.getQuantityAvailable(),
                saved.getPrice(),
                "Product variation created successfully"
        );
    }

    public ViewProductDto getProductById(UUID productId, String sellerEmail) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        if (!product.getSeller().getEmail().equals(sellerEmail)) {
            throw new ValidationException("You are not the owner of this product");
        }
        if (product.isDeleted()) {
            throw new ValidationException("Product has been deleted");
        }
        return new ViewProductDto(product.getId().toString(), product.getName(), product.getDescription(),
                product.getBrand(), product.isCancelable(), product.isReturnable(),
                product.isActive(), product.getCategory().getId(), product.getCategory().getName(),
                product.getSeller().getEmail()
        );
    }

    public ProductVariationViewResponseDto getProductVariationById(String variationId, String sellerEmail) {
        UUID variationUuid;
        try {
            variationUuid = UUID.fromString(variationId);
        } catch (IllegalArgumentException e) {
            throw new InvalidUuidException("Invalid Product Variation ID format");
        }
        ProductVariation variation = productVariationRepository
                .findById(variationUuid)
                .orElseThrow(() -> new NotFoundException("Product variation not found"));
        Product parentProduct = variation.getProduct();
        if (parentProduct.isDeleted()) {
            throw new ValidationException("Parent product has been deleted");
        }
        if (!variation.isActive()) {
            throw new ValidationException("Product variation is not active");
        }
        if (!parentProduct.getSeller().getEmail().equals(sellerEmail)) {
            throw new ValidationException("You are not authorized to view this product variation");
        }
        Map<String, Object> responseMetadata = objectMapper.convertValue(variation.getMetadata(), Map.class);
        return new ProductVariationViewResponseDto(
                variation.getId(), parentProduct.getId(), responseMetadata,
                variation.getQuantityAvailable(), variation.getPrice(),
                variation.getImageName(), parentProduct.getName(),
                parentProduct.getDescription(), parentProduct.getCategory().getName(), parentProduct.getSeller().getEmail()
        );
    }

        public List<ProductListResponseDto> getAllSellerProducts(String sellerEmail, int page, int size) {
            Seller seller = sellerRepository.findByUserEmail(sellerEmail)
                    .orElseThrow(() -> new NotFoundException("Seller not found"));
            Page<Product> productPage = productRepository.findBySellerAndIsDeletedFalse(seller, (Pageable) PageRequest.of(page,size));
            return productPage.stream()
                    .map(product -> {
                        int variationCount = productVariationRepository
                                .countByProductAndIsActiveTrue(product);
                        return new ProductListResponseDto(product.getId().toString(), product.getName(),
                                product.getDescription(), product.getBrand(),
                                product.isCancelable(), product.isReturnable(),
                                product.isActive(), product.getCategory().getId(),
                                product.getCategory().getName(), variationCount
                        );
                    })
                    .collect(Collectors.toList());
        }

        public List<ProductVariationListResponseDto> getProductVariations(String productId, String sellerEmail, int page, int size, String sort, String order) {
            UUID productUuid;
            try {
                productUuid = UUID.fromString(productId);
            } catch (IllegalArgumentException e) {
                throw new InvalidUuidException("Invalid Product ID format");
            }
            Product product = productRepository.findById(productUuid)
                    .orElseThrow(() -> new NotFoundException("Product not found"));
            if (!product.getSeller().getEmail().equals(sellerEmail)) {
                throw new ValidationException("You are not the owner of this product");
            }
            if (product.isDeleted()) {
                throw new ValidationException("Product has been deleted");
            }
            Sort sorting;
            if ("desc".equalsIgnoreCase(order)) {
                sorting = Sort.by(Sort.Direction.DESC, sort);
            } else {
                sorting = Sort.by(Sort.Direction.ASC, sort);
            }
            Pageable pageable = PageRequest.of(page, size, sorting);
            Page<ProductVariation> variationsPage = productVariationRepository.findByProduct(product, pageable);
            List<ProductVariation> variations = variationsPage.getContent();
            List<ProductVariationListResponseDto> variationDtos = new ArrayList<>();
            for (ProductVariation variation : variations) {
                String fullImageUrl = s3ImageService.convertToFullS3Url(variation.getImageName());
                ProductVariationListResponseDto dto = new ProductVariationListResponseDto(
                        variation.getId().toString(),
                        (int) variation.getQuantityAvailable(),
                        variation.getPrice(),
                        variation.getMetadata(),
                        fullImageUrl,
                        variation.isActive()
                );
                variationDtos.add(dto);
            }
            return variationDtos;
        }
// quantity we convert in int as entity have long and productvariation have integer
//seller can see all the products added by him active or inactive both
        @Transactional
        public DeleteProductResponseDto deleteProduct(String productId, String sellerEmail) {
            UUID productUuid;
            try {
                productUuid = UUID.fromString(productId);
            } catch (IllegalArgumentException e) {
                throw new InvalidUuidException("Invalid Product ID format");
            }
            Product product = productRepository.findById(productUuid)
                    .orElseThrow(() -> new NotFoundException("Product not found"));
            if (!product.getSeller().getEmail().equals(sellerEmail)) {
                throw new ValidationException("You are not the owner of this product");
            }
            if (product.isDeleted()) {
                return new DeleteProductResponseDto("Product was already deleted", product.getId().toString(), product.getName(),true
                );
            }
            product.setDeleted(true);
            productRepository.save(product);
            return new DeleteProductResponseDto(
                    "Product deleted successfully",
                    product.getId().toString(),
                    product.getName(),
                    true
            );
        }

    @Transactional
    public UpdateProductResponseDto updateProduct(String productId, String sellerEmail, UpdateProductRequestDto request) {
        UUID productUuid;
        try {
            productUuid = UUID.fromString(productId);
        } catch (IllegalArgumentException e) {
            throw new InvalidUuidException("Invalid Product ID format");
        }
        Product product = productRepository.findById(productUuid)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        if (!product.getSeller().getEmail().equals(sellerEmail)) {
            throw new ValidationException("You are not the owner of this product");
        }
        if (product.isDeleted()) {
            throw new ValidationException("Cannot update deleted product");
        }
        if (request.name() != null && !request.name().equals(product.getName())) {
            boolean nameExists = productRepository.existsByNameAndBrandAndCategoryAndSeller(
                    request.name(), product.getBrand(), product.getCategory(), product.getSeller());
            if (nameExists) {
                throw new DuplicateException("Product name already exists for this brand and category combination");
            }
        }
        if (request.name() != null) {
            product.setName(request.name());
        }
        if (request.description() != null) {
            product.setDescription(request.description());
        }
        if (request.isCancellable() != null) {
            product.setCancelable(request.isCancellable());
        }
        if (request.isReturnable() != null) {
            product.setReturnable(request.isReturnable());
        }
        productRepository.save(product);
        return new UpdateProductResponseDto(
                "Product updated successfully", product.getId().toString(), product.getName(), true
        );
    }


    @Transactional
    public UpdateProductVariationResponseDto updateProductVariation(
            String variationId, String sellerEmail, Float price, Long quantityAvailable,
            String metadataJson, Boolean isActive, MultipartFile imageFile) {
        try {
            UUID variationUuid = UUID.fromString(variationId);
            ProductVariation variation = productVariationRepository.findById(variationUuid)
                    .orElseThrow(() -> new NotFoundException("Product variation not found"));
            Product product = variation.getProduct();
            if (!product.getSeller().getEmail().equals(sellerEmail)) {
                throw new ValidationException("You are not the creator of this product variation");
            }
            if (product.isDeleted()) {
                throw new ValidationException("Cannot update variation of deleted product");
            }
            if (!product.isActive()) {
                throw new ValidationException("Cannot update variation of inactive product");
            }
            if (metadataJson != null && !metadataJson.trim().isEmpty()) {
                Map<String, Object> metadata = objectMapper.readValue(metadataJson,
                        new TypeReference<Map<String, Object>>() {});
                JsonNode metadataNode = objectMapper.convertValue(metadata, JsonNode.class);
                validateMetadataFields(metadataNode, product.getCategory().getId());
                variation.setMetadata(metadataNode);
            }
            if (price != null) variation.setPrice(price);
            if (quantityAvailable != null) variation.setQuantityAvailable(quantityAvailable);
            if (isActive != null) variation.setActive(isActive);
            String updatedImageUrl = variation.getImageName();
            if (imageFile != null && !imageFile.isEmpty()) {
                s3ImageService.validateImage(imageFile);
                updatedImageUrl = s3ImageService.uploadProductVariationImage(imageFile);
                variation.setImageName(updatedImageUrl);
            }
            ProductVariation savedVariation = productVariationRepository.save(variation);
            String fullImageUrl = s3ImageService.convertToFullS3Url(savedVariation.getImageName());
            return new UpdateProductVariationResponseDto(
                    "Product variation updated successfully",
                    savedVariation.getId().toString(),
                    fullImageUrl,
                    savedVariation.getPrice(),
                    savedVariation.getQuantityAvailable(),
                    savedVariation.isActive(),
                    true
            );
        } catch (IllegalArgumentException e) {
            throw new InvalidUuidException("Invalid Product Variation ID format");
        } catch (JsonProcessingException e) {
            throw new ValidationException("Invalid metadata JSON format");
        } catch (IOException e) {
            throw new ValidationException("Image upload failed: " + e.getMessage());
        }
    }

    private void validateMetadataFields(JsonNode metadata, UUID categoryId) {
        if (metadata == null || metadata.size() == 0) {
            throw new ValidationException("Metadata cannot be null or empty");
        }
        List<CategoryMetadataFieldValues> categoryFieldValues =
                categoryMetadataFieldValuesRepository.findByCategoryId(categoryId);
        if (categoryFieldValues.isEmpty()) {
            return;
        }
        Map<String, CategoryMetadataFieldValues> fieldMap = new HashMap<>();
        for (CategoryMetadataFieldValues fieldValue : categoryFieldValues) {
            String fieldName = fieldValue.getCategoryMetadataField().getName().toLowerCase();
            fieldMap.put(fieldName, fieldValue);
        }
        Iterator<Map.Entry<String, JsonNode>> fields = metadata.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String fieldName = entry.getKey();
            JsonNode fieldValue = entry.getValue();
            if (fieldValue == null || fieldValue.isNull() ||
                    (fieldValue.isTextual() && fieldValue.asText().trim().isEmpty())) {
                throw new ValidationException("Field '" + fieldName + "' cannot be null or empty");
            }
            String lowerFieldName = fieldName.toLowerCase();
            CategoryMetadataFieldValues allowedFieldValue = fieldMap.get(lowerFieldName);

            if (allowedFieldValue == null) {
                throw new ValidationException("Unknown field '" + fieldName + "' for this category");
            }
            List<String> allowedValues = allowedFieldValue.getValues();
            if (allowedValues != null && !allowedValues.isEmpty()) {
                String actualValue = fieldValue.asText().trim();

                boolean isValid = false;
                for (String allowedValue : allowedValues) {
                    if (allowedValue.equalsIgnoreCase(actualValue)) {
                        isValid = true;
                        break;
                    }
                }

                if (!isValid) {
                    throw new ValidationException(
                            "Invalid value " + actualValue + " for field " + fieldName +
                                    ". Allowed values: " + allowedValues
                    );
                }
            }
        }
    }
}















