package com.akshay.ecommerce.controllers;
import com.akshay.ecommerce.dto.UpdateProductDto.UpdateProductRequestDto;
import com.akshay.ecommerce.dto.UpdateProductDto.UpdateProductResponseDto;
import com.akshay.ecommerce.dto.*;
import com.akshay.ecommerce.dto.AddProductDto.CreateProductRequestDto;
import com.akshay.ecommerce.dto.AddProductDto.ProductCreatedResponseDto;
import com.akshay.ecommerce.dto.ProductVariation.CreateProductVariationRequestDto;
import com.akshay.ecommerce.dto.ProductVariation.ProductVariationCreatedResponseDto;
import com.akshay.ecommerce.dto.viewCategory.CategoryDetailDto;
import com.akshay.ecommerce.entity.Address;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.InvalidUuidException;
import com.akshay.ecommerce.repository.AddressRepository;
import com.akshay.ecommerce.repository.UserRepository;
import com.akshay.ecommerce.service.ProductService;
import com.akshay.ecommerce.service.SellerProfileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ViewResolver;


import jakarta.validation.Validator;


import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seller")
public class SellerProfileController {
    private final SellerProfileService sellerProfileService;
    private final SellerProfileService sellerService;
    private final ProductService productService;
    private final ViewResolver viewResolver;
    private final AddressRepository addressRepository;
    private final Validator validator;

    private final UserRepository userRepository;

    @PostMapping("/addProduct")
    public ResponseEntity<ProductCreatedResponseDto> createProduct(
            @RequestBody @Valid CreateProductRequestDto req,
            Authentication authentication) {
        String sellerEmail = authentication.getName();
        ProductCreatedResponseDto response = productService.createProduct(req, sellerEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping(value = "/addProductVariations", consumes = {"multipart/form-data"})
    public ResponseEntity<ProductVariationCreatedResponseDto> createProductVariation(
            @RequestParam("productId") @NotBlank String productId,
            @RequestParam("metadata") @NotBlank String metadataJson,
            @RequestParam("price") @NotNull Integer price,
            @RequestParam("quantityAvailable") @NotNull Long quantityAvailable,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Authentication authentication) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> metadata = objectMapper.readValue(metadataJson, new TypeReference<>() {});
            CreateProductVariationRequestDto request = new CreateProductVariationRequestDto();
            request.setProductId(productId);
            request.setMetadata(metadata);
            request.setPrice(price);
            request.setQuantityAvailable(quantityAvailable);
            request.setImageFile(imageFile);
            Set<ConstraintViolation<CreateProductVariationRequestDto>> violations = validator.validate(request);
            if (!violations.isEmpty()) {
                String errorMessages = violations.stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining("; "));
                return ResponseEntity.badRequest().body(
                        new ProductVariationCreatedResponseDto(null, null, null, null, null, null, "Validation failed: " + errorMessages)
                );
            }
            String sellerEmail = authentication.getName();
            ProductVariationCreatedResponseDto response = productService.createProductVariation(request, sellerEmail);
            return ResponseEntity.ok(response);

        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(
                    new ProductVariationCreatedResponseDto(null, null, null, null, null, null, "Invalid metadata JSON format"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new ProductVariationCreatedResponseDto(null, null, null, null, null, null, "Error: " + e.getMessage()));
        }
    }

    @GetMapping("/viewProductById/{id}")
    public ResponseEntity<ViewProductDto> getProduct(@PathVariable String id, Authentication authentication) {
        UUID productId;
        try {
            productId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidUuidException("Invalid product ID format");
        }
        String sellerEmail = authentication.getName();
        ViewProductDto product = productService.getProductById(productId, sellerEmail);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/viewProductVariation/{variationId}")
    public ResponseEntity<ProductVariationViewResponseDto> getProductVariation(@PathVariable String variationId,
            Authentication authentication) {
        String sellerEmail = authentication.getName();
        ProductVariationViewResponseDto response = productService
                .getProductVariationById(variationId, sellerEmail);
        return ResponseEntity.ok(response);
    }

@GetMapping("/viewAllProducts")
public ResponseEntity<List<ProductListResponseDto>> getAllProducts(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
        Authentication authentication) {
    String sellerEmail = authentication.getName();
    List<ProductListResponseDto> products = productService.getAllSellerProducts(sellerEmail, page, size);
    return ResponseEntity.ok(products);
}
// all variation of a single product using product id before we did based on variation id
    @GetMapping("/viewAllProductVariation/{productId}")
    public ResponseEntity<List<ProductVariationListResponseDto>> getProductVariations(
            @PathVariable String productId, @RequestParam( defaultValue = "0") int page,
            @RequestParam( defaultValue = "10") int size, @RequestParam( defaultValue = "price") String sort,
            @RequestParam( defaultValue = "asc") String order, Authentication authentication) {
        String sellerEmail = authentication.getName();
        List<ProductVariationListResponseDto> variations = productService.getProductVariations(productId, sellerEmail, page, size, sort, order);
        return ResponseEntity.ok(variations);
    }

    @DeleteMapping("/deleteProductbyId/{productId}")
    public ResponseEntity<DeleteProductResponseDto> deleteProduct(@PathVariable String productId, Authentication authentication) {
        String sellerEmail = authentication.getName();
        DeleteProductResponseDto response = productService.deleteProduct(productId, sellerEmail);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/updateProduct/{productId}")
    public ResponseEntity<UpdateProductResponseDto> updateProduct(@PathVariable String productId,
            @Valid @RequestBody UpdateProductRequestDto request, Authentication authentication) {
        String sellerEmail = authentication.getName();
        UpdateProductResponseDto response = productService.updateProduct(productId, sellerEmail, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/updateProductVariation/{variationId}", consumes = "multipart/form-data")
    public ResponseEntity<UpdateProductVariationResponseDto> updateProductVariation(
            @PathVariable String variationId,
            @RequestParam(required = false) Float price,
            @RequestParam(required = false) Long quantityAvailable,
            @RequestParam(required = false) String metadataJson,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) MultipartFile imageFile,
            Authentication authentication) {

        try {
            Map<String, Object> metadata = metadataJson != null && !metadataJson.trim().isEmpty() ? new ObjectMapper().readValue(metadataJson, new TypeReference<>() {}) : null;
            UpdateProductBySellerDto req = new UpdateProductBySellerDto();
            req.setPrice(price);
            req.setQuantityAvailable(quantityAvailable);
            req.setMetadata(metadata);
            req.setIsActive(isActive);
            req.setImageFile(imageFile);
            Set<ConstraintViolation<UpdateProductBySellerDto>> violations = validator.validate(req);
            if (!violations.isEmpty()) {
                String errorMessages = violations.stream().map(ConstraintViolation::getMessage).collect(Collectors.joining("; "));
                return ResponseEntity.badRequest().body(
                        new UpdateProductVariationResponseDto(
                                "Validation failed: " + errorMessages, null, null, null, null, null, false
                        )
                );
            }
            String sellerEmail = authentication.getName();
            UpdateProductVariationResponseDto response = productService.updateProductVariation(
                    variationId, sellerEmail, price, quantityAvailable, metadataJson, isActive, imageFile);
            return ResponseEntity.ok(response);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body(
                    new UpdateProductVariationResponseDto("Invalid metadata JSON format", null, null, null, null, null, false)
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    new UpdateProductVariationResponseDto("Error: " + e.getMessage(), null, null, null, null, null, false)
            );
        }
    }


    @GetMapping("/viewCategories")
    public ResponseEntity<List<CategoryDetailDto>> getAllLeafCategories() {
        return ResponseEntity.ok(sellerService.getCategories());
    }

    @GetMapping("/viewProfile")
    public ResponseEntity<sellerProfileImageDto> getProfile(Authentication authentication) {
        sellerProfileImageDto dto = sellerProfileService.viewMyProfile(authentication);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<SellerProfileDto> updateProfile(Authentication authentication, @Valid @RequestBody SellerProfileUpdateDto req) {
        SellerProfileDto dto = sellerProfileService.updateProfile(authentication, req);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/changePassword")
    public ResponseEntity<Map<String,String>> changePassword(Authentication authentication, @Valid @RequestBody ChangePasswordRequestDto req) {
        sellerProfileService.changePassword(authentication, req);
        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }


    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<Map<String,String>> updatingAddress(Authentication authentication, @PathVariable String addressId,
            @Valid @RequestBody AddressUpdateDto req) {
        UUID uuid;
        try {
            uuid = UUID.fromString(addressId);
        } catch (IllegalArgumentException e) {
            throw new InvalidUuidException("Invalid Address ID format");
        }
        sellerProfileService.updateAddress(authentication, uuid, req);
        return ResponseEntity.ok(Map.of("message", "Address updated successfully"));
    }
}
