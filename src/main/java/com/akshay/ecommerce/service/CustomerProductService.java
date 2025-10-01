package com.akshay.ecommerce.service;
import com.akshay.ecommerce.dto.CustomerProductDtos.*;
import com.akshay.ecommerce.entity.Category;
import com.akshay.ecommerce.entity.Product;
import com.akshay.ecommerce.entity.ProductVariation;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.InvalidUuidException;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.NotFoundException;
import com.akshay.ecommerce.repository.CategoryRepository;
import com.akshay.ecommerce.repository.ProductRepository;
import com.akshay.ecommerce.repository.ProductVariationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerProductService {
    private final ProductRepository productRepository;
    private final S3ImageService s3ImageService;
    private final ObjectMapper objectMapper;
    private final CategoryRepository categoryRepository;
    private final ProductVariationRepository productVariationRepository;
    public CustomerProductViewResponseDto getProductById(String productId) {
        UUID productUuid;
        try {
            productUuid = UUID.fromString(productId);
        } catch (IllegalArgumentException e) {
            throw new InvalidUuidException("Invalid Product ID format");
        }
        Product product = productRepository.findById(productUuid)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        if (product.isDeleted()) {
            throw new NotFoundException("Product not found");
        }
        if (!product.isActive()) {
            throw new ValidationException("Product is not active");
        }
        List<ProductVariation> activeVariations = new ArrayList<>();
        List<ProductVariation> allVariations = product.getProductVariations();

        for (ProductVariation variation : allVariations) {
            if (variation.isActive()) {
                activeVariations.add(variation);
            }
        }
        if (activeVariations.isEmpty()) {
            throw new ValidationException("Product has no valid variations");
        }
        CategoryDetailsDto categoryDto = new CategoryDetailsDto(
                product.getCategory().getId().toString(),
                product.getCategory().getName()
        );
        List<CustomerProductVariationDto> variationDtos = new ArrayList<>();
        for (ProductVariation variation : activeVariations) {
            CustomerProductVariationDto variationDto = convertToVariationDto(variation);
            variationDtos.add(variationDto);
        }
        return new CustomerProductViewResponseDto(
                product.getId().toString(),
                product.getName(),
                product.getDescription(),
                product.getBrand(),
                product.isCancelable(),
                product.isReturnable(),
                categoryDto,
                variationDtos
        );
    }
    private CustomerProductVariationDto convertToVariationDto(ProductVariation variation) {
        Map<String, Object> metadata = objectMapper.convertValue(variation.getMetadata(), Map.class);
        String fullImageUrl = s3ImageService.convertToFullS3Url(variation.getImageName());
        return new CustomerProductVariationDto(
                variation.getId().toString(),
                metadata,
                variation.getPrice(),
                variation.getQuantityAvailable(),
                fullImageUrl
        );
    }


public CustomerProductListResponseDto getProductsByCategory(String categoryId) {
    UUID categoryUuid;
    try {
        categoryUuid = UUID.fromString(categoryId);
    } catch (IllegalArgumentException e) {
        throw new InvalidUuidException("Invalid Category ID format");
    }
    Category category = categoryRepository.findById(categoryUuid)
            .orElseThrow(() -> new NotFoundException("Category not found"));
    List<Category> childCategories = categoryRepository.findByParent_Id(categoryUuid);
    List<Product> allProducts = new ArrayList<>();
    if (childCategories.isEmpty()) {
        allProducts = productRepository.findByCategoryAndIsActiveTrueAndIsDeletedFalse(category);
    } else {
        List<Category> allChildCategories = new ArrayList<>();
        allChildCategories.add(category);
        allChildCategories.addAll(childCategories);
        getAllDescendantCategories(childCategories, allChildCategories);

        for (Category category1 : allChildCategories) {
            List<Product> categoryProducts = productRepository.findByCategoryAndIsActiveTrueAndIsDeletedFalse(category1);
            for (Product product : categoryProducts) {
                boolean exists = false;
                for (Product existing : allProducts) {
                    if (existing.getId().equals(product.getId())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    allProducts.add(product);
                }
            }
        }
    }

    List<CustomerProductSummaryDto> productSummaries = new ArrayList<>();
    for (Product product : allProducts) {
        List<ProductVariation> activeVariations = new ArrayList<>();
        for (ProductVariation variation : product.getProductVariations()) {
            if (variation.isActive()) {
                activeVariations.add(variation);
            }
        }
        if (!activeVariations.isEmpty()) {
            List<String> variationImages = new ArrayList<>();
            for (ProductVariation variation : activeVariations) {
                String imageUrl = s3ImageService.convertToFullS3Url(variation.getImageName());
                variationImages.add(imageUrl);
            }

            CustomerProductSummaryDto productSummary = new CustomerProductSummaryDto(
                    product.getId().toString(),
                    product.getName(),
                    product.getDescription(),
                    product.getBrand(),
                    product.isCancelable(),
                    product.isReturnable(),
                    null,
                    null,
                    variationImages,
                    activeVariations.size(),
                    product.isActive()
            );
            productSummaries.add(productSummary);
        }
    }

    CategoryDetailsDto categoryDto = new CategoryDetailsDto(
            category.getId().toString(),
            category.getName()
    );

    return new CustomerProductListResponseDto(
            productSummaries,
            categoryDto
    );
}
    private void getAllDescendantCategories(List<Category> childCategories, List<Category> allCategories) {
        for (Category child : childCategories) {
            List<Category> grandChildren = categoryRepository.findByParent_Id(child.getId());
            if (!grandChildren.isEmpty()) {
                allCategories.addAll(grandChildren);
                getAllDescendantCategories(grandChildren, allCategories);
            }
        }
    }

    public CustomerProductListResponseDto getSimilarProducts(String productId) {

            UUID productUuid;
            try {
                productUuid = UUID.fromString(productId);
            } catch (IllegalArgumentException e) {
                throw new InvalidUuidException("Invalid Product ID format");
            }

            Product currentProduct = productRepository.findById(productUuid)
                    .orElseThrow(() -> new NotFoundException("Product not found"));

            if (currentProduct.isDeleted() || !currentProduct.isActive()) {
                throw new NotFoundException("Product not available");
            }

            List<Product> similarProducts = new ArrayList<>();
            List<Product> categoryProducts = productRepository
                    .findByCategoryAndIsActiveTrueAndIsDeletedFalse(currentProduct.getCategory());
            similarProducts.addAll(categoryProducts);
            List<Product> brandProducts = productRepository
                    .findByBrandAndIsActiveTrueAndIsDeletedFalse(currentProduct.getBrand());

            for (Product product : brandProducts) {
                boolean exists = false;
                for (Product existing : similarProducts) {
                    if (existing.getId().equals(product.getId())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    similarProducts.add(product);
                }
            }
            List<Product> finalSimilarProducts = new ArrayList<>();
            for (Product product : similarProducts) {
                if (!product.getId().equals(currentProduct.getId())) {
                    finalSimilarProducts.add(product);
                }
            }
            List<CustomerProductSummaryDto> productSummaries = new ArrayList<>();
            for (Product product : finalSimilarProducts) {
                List<ProductVariation> activeVariations = new ArrayList<>();
                for (ProductVariation variation : product.getProductVariations()) {
                    if (variation.isActive()) {
                        activeVariations.add(variation);
                    }
                }

                if (!activeVariations.isEmpty()) {
                    List<String> variationImages = new ArrayList<>();
                    for (ProductVariation variation : activeVariations) {
                        String imageUrl = s3ImageService.convertToFullS3Url(variation.getImageName());
                        variationImages.add(imageUrl);
                    }

                    CustomerProductSummaryDto productSummary = new CustomerProductSummaryDto(
                            product.getId().toString(),
                            product.getName(),
                            product.getDescription(),
                            product.getBrand(),
                            product.isCancelable(),
                            product.isReturnable(),
                            null,
                            null,
                            variationImages,
                            activeVariations.size(),
                            product.isActive()
                    );
                    productSummaries.add(productSummary);
                }
            }
            CategoryDetailsDto categoryDto = new CategoryDetailsDto(
                    currentProduct.getCategory().getId().toString(),
                    currentProduct.getCategory().getName()
            );
            return new CustomerProductListResponseDto(productSummaries, categoryDto);
        }
}