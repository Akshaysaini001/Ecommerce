package com.akshay.ecommerce.controllers;
import com.akshay.ecommerce.dto.ProductActivate.AdminProductActivationResponseDto;
import com.akshay.ecommerce.dto.ProductActivate.AdminProductDeactivationResponseDto;
import com.akshay.ecommerce.dto.UpdateCategory.CategoryUpdateRequestDto;
import com.akshay.ecommerce.dto.UpdateCategory.CategoryUpdateResponseDto;
import com.akshay.ecommerce.dto.viewCategory.CategoryDetailDto;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.InvalidUuidException;
import com.akshay.ecommerce.service.CategoryAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.akshay.ecommerce.dto.*;
import com.akshay.ecommerce.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.endpoints.internal.Value;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")

public class AdminController {
    private final AdminService adminService;
    private final CategoryAdminService categoryAdminService;


@PutMapping("/product/Activate/{productId}")
public ResponseEntity<AdminProductActivationResponseDto> activateProduct(
        @PathVariable String productId,
        Principal principal) {
    String adminEmail = principal.getName();
    AdminProductActivationResponseDto response = adminService.activateProduct(productId, adminEmail);
    return ResponseEntity.ok(response);}


    @PutMapping("/product/DeActivate/{productId}")
    public ResponseEntity<AdminProductDeactivationResponseDto> deactivateProduct(
            @PathVariable String productId,
            Principal principal) {
        String adminEmail = principal.getName();
        AdminProductDeactivationResponseDto response = adminService.deactivateProduct(productId, adminEmail);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/viewProduct/{productId}")
    public ResponseEntity<AdminProductViewResponseDto> getProduct(@PathVariable String productId) {
        AdminProductViewResponseDto response = adminService.getProductById(productId);
        return ResponseEntity.ok(response);
    }



    @GetMapping("/viewAllProducts")
    public ResponseEntity<AdminProductListResponseDto> getAllProducts(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String sellerId,
            @RequestParam(required = false) String query) {
        AdminProductListResponseDto response = adminService.getAllProducts(categoryId, sellerId, query);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/AddMetadataField")
    public ResponseEntity<Map<String, Object>> addMetadataField(@Valid @RequestBody MetadataFieldDto metadataField) {
        UUID newFieldId = categoryAdminService.addMetadataField(metadataField);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Successfully added MetadataField");
        response.put("fieldId", newFieldId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/getMetadataFields")
    public ResponseEntity<Page<MetadataFieldViewDto>> viewAllMetaDatafields(@RequestParam(defaultValue = "0") int pageOffset, @RequestParam(defaultValue = "10") int pageSize,
                                                                            @RequestParam(defaultValue = "name") String sortField, @RequestParam(defaultValue = "asc") String sortOrder) {
        Page<MetadataFieldViewDto> fieldsPage = categoryAdminService.getAllMetadataFields(pageOffset, pageSize, sortField, sortOrder);
        return ResponseEntity.ok(fieldsPage);
    }
    @PostMapping("/addCategory")
    public ResponseEntity<Map<String, UUID>> addCategory(@Valid @RequestBody AddCategoryDto addCategoryDto) {
        UUID newCategoryId = categoryAdminService.addCategory(addCategoryDto);
        return new ResponseEntity<>(Map.of("categoryId", newCategoryId), HttpStatus.CREATED);
    }
    @GetMapping("/viewCategory/{id}")
    public ResponseEntity<CategoryDetailDto> getCategoryById(@PathVariable String id) {
        UUID categoryId;
        try {
            categoryId = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new InvalidUuidException("Invalid UUID format: " + id);
        }
        CategoryDetailDto categoryDetails = categoryAdminService.getCategoryDetails(categoryId);
        return ResponseEntity.ok(categoryDetails);
    }

    @GetMapping("/viewCategories")
    public ResponseEntity<List<CategoryDetailDto>> getAllCategories(    @RequestParam(defaultValue = "0") int offset, @RequestParam(defaultValue = "10") int max,  @RequestParam(defaultValue = "name") String sort,
                                                                    @RequestParam(defaultValue = "asc") String order, @RequestParam(required = false) String query) {
        List<CategoryDetailDto> categories = categoryAdminService.getAllCategoriesWithDetails(max, offset, sort, order, query);
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/updateCategory")
    public ResponseEntity<CategoryUpdateResponseDto> updateCategory(
            @Valid @RequestBody CategoryUpdateRequestDto requestDto) {
//actual validation method call me hai isliye unused show krrha hai ye
        UUID categoryId;
        try {
            categoryId = UUID.fromString(requestDto.id());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new CategoryUpdateResponseDto(false, "Validation failed",
                            List.of("Invalid UUID format: " + requestDto.id()))
            );
        }
        CategoryUpdateResponseDto response = categoryAdminService.updateCategory(requestDto);
        return response.success() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }


    @PostMapping("/addCategoryMetadata")
    public ResponseEntity<AddCategoryMetadataResponseDto> addCategoryMetadata(
            @RequestBody AddCategoryMetadataRequestDto request) {
        UUID categoryId;
        try {
            categoryId = UUID.fromString(request.getCategoryId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new AddCategoryMetadataResponseDto(false, "Invalid UUID ",
                            List.of("Invalid UUID format for Category ID: " + request.getCategoryId()))
            );
        }

        UUID metadataFieldId;
        try {
            metadataFieldId = UUID.fromString(request.getMetadataFieldId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new AddCategoryMetadataResponseDto(false, "Validation failed",
                            List.of("Invalid UUID format for Metadata Field ID: " + request.getMetadataFieldId()))
            );
        }
        AddCategoryMetadataResponseDto response = categoryAdminService.addCategoryMetadata(request);
        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }

    @PutMapping("/updateCategoryMetadata")
    public ResponseEntity<AddCategoryMetadataResponseDto> updateCategoryMetadata(
            @RequestBody AddCategoryMetadataRequestDto request) {
        UUID categoryId;
        try {
            categoryId = UUID.fromString(request.getCategoryId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new AddCategoryMetadataResponseDto(false, "Validation failed",
                            List.of("Invalid UUID format for Category ID: " + request.getCategoryId()))
            );
        }
        UUID metadataFieldId;
        try {
            metadataFieldId = UUID.fromString(request.getMetadataFieldId());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new AddCategoryMetadataResponseDto(false, "Validation failed",
                            List.of("Invalid UUID format for Metadata Field ID: " + request.getMetadataFieldId()))
            );
        }
        AddCategoryMetadataResponseDto response = categoryAdminService
                .updateCategoryMetadata(request);

        return response.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }


    @PutMapping("/customers/activate/{userId}")
    public ResponseEntity<Map<String, String>> activateCustomer(@PathVariable String userId) {
        UUID uuid;
        try {
            uuid = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            throw new InvalidUuidException("Invalid Customer ID format: " + userId);
        }

        String message = adminService.activateCustomer(uuid);
        return ResponseEntity.ok(Map.of("message", message));
    }

//    @PutMapping("/customers/activate/{userId}")
//    public ResponseEntity<Map<String, String>> activateCustomer(@PathVariable UUID userId) {
//        String message = adminService.activateCustomer(userId);
//        return ResponseEntity.ok(Map.of("message", message));
//    }
    @PutMapping("/customers/deactivate/{userId}")
    public ResponseEntity<Map<String, String>> deactivateCustomer(@PathVariable String userId) {
        UUID uuid;
        try{
            uuid = UUID.fromString(userId);
        }
        catch (IllegalArgumentException e){
            throw new InvalidUuidException("Invalid Customer ID format: " + userId);
        }
        String message = adminService.deactivateCustomer(uuid);
        return ResponseEntity.ok(Map.of("message", message));
    }
    @PutMapping("/sellers/activate/{userId}")
    public ResponseEntity<Map<String, String>> activateSeller(@PathVariable String userId) {
    UUID uuid;
    try{
        uuid = UUID.fromString(userId);
    }  catch (IllegalArgumentException e){
        throw new InvalidUuidException("Invalid Customer ID format: " + userId);
    }
        String message = adminService.activateSeller(uuid);
        return ResponseEntity.ok(Map.of("message", message));
    }
    @PutMapping("/sellers/deactivate/{userId}")
    public ResponseEntity<Map<String, String>> deactivateSeller(@PathVariable String userId) {
        UUID uuid;
        try{
            uuid = UUID.fromString(userId);
        }  catch (IllegalArgumentException e){
            throw new InvalidUuidException("Invalid Customer ID format: " + userId);
        }
        String message = adminService.deactivateSeller(uuid);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @GetMapping("/customers")
    public ResponseEntity<Page<CustomerListDto>> listCustomers( @RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "0")  int pageOffset,
            @RequestParam(defaultValue = "createdBy") String sortField,
            @RequestParam(required = false)    String email
    ){
        Page<CustomerListDto> pageList = adminService.getAllCustomers(pageOffset, pageSize, sortField, email);
        return ResponseEntity.status(HttpStatus.OK).body(pageList);
    }

     @GetMapping("/sellers")
     public ResponseEntity<Page<SellerListDto>> getAllSellers(
        @RequestParam(defaultValue = "0") int pageOffset,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(defaultValue = "id") String sort,
        @RequestParam(required = false) String email
     ) {
        Page<SellerListDto> sellers = adminService.getAllSellers(pageOffset, pageSize, sort, email);
        return ResponseEntity.ok(sellers);
     }

}


