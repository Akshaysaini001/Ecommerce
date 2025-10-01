package com.akshay.ecommerce.controllers;
import com.akshay.ecommerce.dto.*;
import com.akshay.ecommerce.dto.CustomerProductDtos.CategoryDetailsDto;
import com.akshay.ecommerce.dto.CustomerProductDtos.CustomerProductListResponseDto;
import com.akshay.ecommerce.dto.CustomerProductDtos.CustomerProductVariationDto;
import com.akshay.ecommerce.dto.CustomerProductDtos.CustomerProductViewResponseDto;
import com.akshay.ecommerce.dto.viewCategory.CategoryBriefDto;
import com.akshay.ecommerce.dto.viewCategory.CategoryDetailDto;
import com.akshay.ecommerce.entity.Product;
import com.akshay.ecommerce.entity.ProductVariation;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.InvalidUuidException;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.NotFoundException;
import com.akshay.ecommerce.repository.ProductRepository;
import com.akshay.ecommerce.service.CustomerProductService;
import com.akshay.ecommerce.service.CustomerProfileService;
import com.akshay.ecommerce.service.S3ImageService;
import com.akshay.ecommerce.service.SellerProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer")
public class customerProfileController {
    private final CustomerProfileService customerProfileService;
    private final CustomerProductService customerProductService;



    @GetMapping("/viewCategories")
    public ResponseEntity<List<CategoryBriefDto>> listCategories() {
        return ResponseEntity.ok(customerProfileService.listCategories());
    }


//    @GetMapping("/viewCategories")
//    public ResponseEntity<List<CategoryBriefDto>> listCategories(
//            @RequestParam(name = "categoryId", required = false) String uuid) {
//        UUID id = null;
//        if (uuid != null && !uuid.isBlank()) {
//            try {
//                id = UUID.fromString(uuid);
//            } catch (IllegalArgumentException ex) {
//                throw new InvalidUuidException("Invalid UUID format: " + uuid);
//            }
//        }
//        return ResponseEntity.ok(customerProfileService.listCategories(id));
//    }

    @GetMapping("/filterDetailOfaCategory/{uuid}")
    public ResponseEntity<CategoryFilterDto> fetchFilters(@PathVariable String uuid) {
        UUID id = null;
        if (uuid != null && !uuid.isBlank()) {
            try {
                id = UUID.fromString(uuid);
            } catch (IllegalArgumentException ex) {
                throw new InvalidUuidException("Invalid UUID format: " + uuid);
            }
        }
        CategoryFilterDto filters = customerProfileService.customerFilterDetailForCategory(id);
        return ResponseEntity.ok(filters);
    }

    @GetMapping("/viewProfile")
    public ResponseEntity<CustomerProfileImageDto> getProfile(Authentication authentication) {
        CustomerProfileImageDto dto = customerProfileService.viewCustomerProfile(authentication);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/viewAddresses")
    public ResponseEntity<List<CustomerViewAddressesDto>> viewMyAddresses(Authentication authentication) {
    List<CustomerViewAddressesDto> addresses = customerProfileService.getAddresses(authentication);
    return ResponseEntity.ok(addresses);
   }

    @PutMapping("/updateProfile")
    public ResponseEntity<Map<String, String>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody CustomerProfileUpdateDto req) {

        customerProfileService.updateProfile(authentication, req);
        return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));

    }
    @PutMapping("/changePassword")
    public ResponseEntity<Map<String,String>> changePassword(Authentication authentication, @Valid @RequestBody ChangePasswordRequestDto req) {
        customerProfileService.changePassword(authentication, req);
        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }
    @PostMapping("/addAddress")
    public ResponseEntity<Map<String, String>> addingAddress(Authentication auth, @Valid @RequestBody AddressCreateDto req) {
        customerProfileService.addAddress(auth, req);
        return new ResponseEntity<>(Map.of("message", "Address added successfully"), HttpStatus.CREATED);
    }


    @DeleteMapping("/deleteAddress/{addressId}")
    public ResponseEntity<Map<String, String>> deleteAddress(Authentication authentication, @PathVariable String addressId) {
        UUID uuid;
        try {
            uuid = UUID.fromString(addressId);
        } catch (IllegalArgumentException e) {
            throw new InvalidUuidException("Invalid Address ID format");
        }
        customerProfileService.deleteAddress(authentication, uuid);
        return ResponseEntity.ok(Map.of("message", "Address deleted successfully"));
    }

    @PutMapping("/updateAddress/{addressId}")
    public ResponseEntity<Map<String, String>> updateAddress(
            Authentication authentication,
            @PathVariable String addressId,
            @Valid @RequestBody AddressCreateDto req) {
        UUID uuid;
        try {
            uuid = UUID.fromString(addressId);
        } catch (IllegalArgumentException e) {
            throw new InvalidUuidException("Invalid Address ID format");
        }
        customerProfileService.updateAddress(authentication, uuid, req);
        return ResponseEntity.ok(Map.of("message", "Address updated successfully"));
    }

    @GetMapping("/viewaProduct/{productId}")
    public ResponseEntity<CustomerProductViewResponseDto> getProduct(@PathVariable String productId) {
        CustomerProductViewResponseDto response = customerProductService.getProductById(productId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/viewAllProductsInaCategory/{categoryId}")
    public ResponseEntity<CustomerProductListResponseDto> getProductsByCategory(@PathVariable String categoryId) {
        CustomerProductListResponseDto response = customerProductService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/viewSimilarProduct/{productId}")
    public ResponseEntity<CustomerProductListResponseDto> getSimilarProducts(@PathVariable String productId) {
        CustomerProductListResponseDto response = customerProductService.getSimilarProducts(productId);
        return ResponseEntity.ok(response);
    } // find it by product in same category in which this product id is and also do it with the brand type

}

















