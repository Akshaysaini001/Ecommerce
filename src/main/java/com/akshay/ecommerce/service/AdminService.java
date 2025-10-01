package com.akshay.ecommerce.service;
import com.akshay.ecommerce.dto.*;
import com.akshay.ecommerce.dto.CustomerProductDtos.CategoryDetailsDto;
import com.akshay.ecommerce.dto.ProductActivate.AdminProductActivationResponseDto;
import com.akshay.ecommerce.dto.ProductActivate.AdminProductDeactivationResponseDto;
import com.akshay.ecommerce.entity.*;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.InvalidUuidException;
import com.akshay.ecommerce.repository.CategoryRepository;
import com.akshay.ecommerce.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import org.springframework.data.domain.Pageable;
import com.akshay.ecommerce.repository.CustomerRepository;
import com.akshay.ecommerce.repository.SellerRepository;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminService {
    private final CustomerRepository customerRepository;
    private final SellerRepository sellerRepository;
    private final EmailService emailService;
    private final ProductRepository productRepository;
    private final S3ImageService s3ImageService;
    private final ObjectMapper objectMapper;


    public Page<CustomerListDto> getAllCustomers(int pageOffset, int pageSize, String sortField, String email) {
        pageOffset = Math.max(0, pageOffset);
        pageSize = pageSize > 0 ? pageSize : 10;

        Pageable pageable = PageRequest.of(pageOffset, pageSize, Sort.by(Sort.Direction.ASC, sortField));
        if (email != null ) {
            return customerRepository.findByEmailContainingIgnoreCase(email.trim(), pageable)
                    .map(this::convertToCustomerListDto);
        } else {
            return customerRepository.findAll(pageable)
                    .map(this::convertToCustomerListDto);
        }
    }
    private CustomerListDto convertToCustomerListDto(Customer c) {
        String fn = c.getFirstName();
        String ln = c.getLastName();
        String fullName = (fn + " " + ln);
        return new CustomerListDto(c.getId(), fullName, c.getEmail(), c.isActive());
    }

    @Transactional
    public Page<SellerListDto> getAllSellers(int pageOffset, int pageSize, String sortBy, String email) {
        int p = Math.max(0, pageOffset);
        int s = pageSize > 0 ? pageSize : 10;
        Pageable pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.ASC, sortBy));
        Page<Seller> sellers;
        if (email != null && !email.trim().isEmpty()) {
            sellers = sellerRepository.findByEmailContainingIgnoreCase(email.trim(), pageable);
        } else {
            sellers = sellerRepository.findAll(pageable);
        }
        return sellers.map(this::convertToSellerListDto);
    }

    @Transactional
    public String activateCustomer(UUID userId) {
        Customer customer = customerRepository.findById(userId).orElseThrow(() -> new NotFoundException("Customer not found"));
        if (customer.isActive()) {
            return "Customer already active";
        }
        customer.setActive(true);
        customerRepository.save(customer);
        emailService.sendCustomerActivationEmail(customer.getEmail(), customer.getFirstName());
        return "Customer activated successfully";
    }





    @Transactional
    public String deactivateCustomer(UUID userId) {
        Customer customer = customerRepository.findById(userId).orElseThrow(() -> new NotFoundException("Customer not found"));
        if (!customer.isActive()) {
            return "Customer is already deactivated";
        }
        customer.setActive(false);
        customerRepository.save(customer);
            emailService.sendCustomerDeactivationEmail(customer.getEmail(), customer.getFirstName());
        return "Customer deactivated successfully";
    }

    @Transactional
    public String activateSeller(UUID userId) {
        Seller seller = sellerRepository.findById(userId).orElseThrow(() -> new NotFoundException("Seller not found"));
        if (seller.isActive()) {
            return "Seller is already active";
        }
        seller.setActive(true);
        sellerRepository.save(seller);
        emailService.sendSellerActivationEmail(seller.getEmail(), seller.getFirstName(), seller.getCompanyName());
        return "Seller activated successfully";
    }

    @Transactional
    public String deactivateSeller(UUID userId) {
        Seller seller = sellerRepository.findById(userId).orElseThrow(() -> new NotFoundException("Seller not found"));
        if (!seller.isActive()) {
            return "Seller is already deactivated";
        }
        seller.setActive(false);
        sellerRepository.save(seller);
        emailService.sendSellerDeactivationEmail(seller.getEmail(), seller.getFirstName(), seller.getCompanyName());
        return "Seller deactivated successfully";
    }

    private SellerListDto convertToSellerListDto(Seller seller) {
        String companyAddress = "";
        if (seller.getAddresses() != null && !seller.getAddresses().isEmpty()) {
            var address = seller.getAddresses().get(0);
            companyAddress = address.getAddressLine() + ", " + address.getCity() + ", " + address.getState();
        }
        return new SellerListDto(
                seller.getId(),
                seller.getFirstName() + " " + seller.getLastName(),
                seller.getEmail(),
                seller.isActive(),
                seller.getCompanyName(),
                companyAddress,
                seller.getCompanyContact()
        );
    }

        public AdminProductViewResponseDto getProductById(String productId) {
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

            List<ProductVariation> allVariations = product.getProductVariations();

            List<AdminProductVariationDto> variationDtos = new ArrayList<>();
            for (ProductVariation variation : allVariations) {
                AdminProductVariationDto variationDto = convertToVariationDto(variation);
                variationDtos.add(variationDto);
            }

            CategoryDetailsDto categoryDto = new CategoryDetailsDto(
                    product.getCategory().getId().toString(),
                    product.getCategory().getName()
            );

            return new AdminProductViewResponseDto(
                    product.getId().toString(),
                    product.getName(),
                    product.getDescription(),
                    product.getBrand(),
                    product.isCancelable(),
                    product.isReturnable(),
                    product.isActive(),
                    categoryDto,
                    variationDtos
            );
        }

        private AdminProductVariationDto convertToVariationDto(ProductVariation variation) {
            Map<String, Object> metadata = objectMapper.convertValue(variation.getMetadata(), Map.class);
            String fullImageUrl = s3ImageService.convertToFullS3Url(variation.getImageName());

            return new AdminProductVariationDto(
                    variation.getId().toString(),
                    metadata,
                    variation.getPrice(),
                    variation.getQuantityAvailable(),
                    fullImageUrl,
                    variation.isActive()
            );
        }


    @Transactional
    public AdminProductActivationResponseDto activateProduct(String productId, String adminEmail) {
        UUID productUuid;
        try {
            productUuid = UUID.fromString(productId);
        } catch (IllegalArgumentException e) {
            throw new InvalidUuidException("Invalid Product ID format");
        }
        Product product = productRepository.findById(productUuid)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        if (product.isDeleted()) {
            throw new ValidationException("Cannot activate deleted product");
        }
        if (product.isActive()) {
            return new AdminProductActivationResponseDto(
                    "Product is already active", product.getId().toString(),
                    product.getName(), true, product.getSeller().getEmail()
            );
        }
        product.setActive(true);
        productRepository.save(product);
        emailService.sendProductActivationNotification(
                product.getSeller().getEmail(), product.getName(),
                product.getBrand(), adminEmail
        );
        return new AdminProductActivationResponseDto(
                "Product activated successfully", product.getId().toString(),
                product.getName(), true, product.getSeller().getEmail()
        );
    }



    @Transactional
    public AdminProductDeactivationResponseDto deactivateProduct(String productId, String adminEmail) {
        UUID productUuid;
        try {
            productUuid = UUID.fromString(productId);
        } catch (IllegalArgumentException e) {
            throw new InvalidUuidException("Invalid Product ID format");
        }
        Product product = productRepository.findById(productUuid)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        if (product.isDeleted()) {
            throw new ValidationException("Cannot deactivate deleted product");
        }
        if (!product.isActive()) {
            return new AdminProductDeactivationResponseDto(
                    "Product is already inactive", product.getId().toString(), product.getName(),
                    false, product.getSeller().getEmail()
            );
        }
        product.setActive(false);
        productRepository.save(product);
        emailService.sendProductDeactivationNotification(
                product.getSeller().getEmail(), product.getName(), product.getBrand(), adminEmail
        );
        return new AdminProductDeactivationResponseDto(
                "Product deactivated successfully", product.getId().toString(), product.getName(),
                true, product.getSeller().getEmail()
        );
    }


    private final CategoryRepository categoryRepository;
        public AdminProductListResponseDto getAllProducts(String categoryId, String sellerId, String query) {
            List<Product> allProducts = new ArrayList<>();
            if (categoryId != null && !categoryId.trim().isEmpty()) {
                UUID categoryUuid;
                try {
                    categoryUuid = UUID.fromString(categoryId);
                } catch (IllegalArgumentException e) {
                    throw new InvalidUuidException("Invalid Category ID format");
                }

                Category category = categoryRepository.findById(categoryUuid)
                        .orElseThrow(() -> new NotFoundException("Category not found"));

                List<Category> childCategories = categoryRepository.findByParent_Id(categoryUuid);

                if (childCategories.isEmpty()) {
                    allProducts = productRepository.findByCategoryAndIsDeletedFalse(category);
                } else {
                    List<Category> allChildCategories = new ArrayList<>();
                    allChildCategories.add(category);
                    allChildCategories.addAll(childCategories);
                    getAllDescendantCategories(childCategories, allChildCategories);

                    for (Category cat : allChildCategories) {
                        List<Product> categoryProducts = productRepository.findByCategoryAndIsDeletedFalse(cat);

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
            } else if (sellerId != null && !sellerId.trim().isEmpty()) {
                UUID sellerUuid;
                try {
                    sellerUuid = UUID.fromString(sellerId);
                } catch (IllegalArgumentException e) {
                    throw new InvalidUuidException("Invalid Seller ID format");
                }

                Seller seller = sellerRepository.findById(sellerUuid)
                        .orElseThrow(() -> new NotFoundException("Seller not found"));

                allProducts = productRepository.findBySellerAndIsDeletedFalse(seller);
            } else {
                allProducts = productRepository.findByIsDeletedFalse();
            }
            if (query != null && !query.trim().isEmpty()) {
                List<Product> filteredProducts = new ArrayList<>();
                String searchQuery = query.trim().toLowerCase();

                for (Product product : allProducts) {
                    if (product.getName().toLowerCase().contains(searchQuery) ||
                            product.getBrand().toLowerCase().contains(searchQuery)) {
                        filteredProducts.add(product);
                    }
                }
                allProducts = filteredProducts;
            }

            List<AdminProductSummaryDto> productSummaries = new ArrayList<>();

            for (Product product : allProducts) {
                List<ProductVariation> allVariations = product.getProductVariations();
                List<String> variationImages = new ArrayList<>();
                int activeVariationCount = 0;
                for (ProductVariation variation : allVariations) {
                    String imageUrl = s3ImageService.convertToFullS3Url(variation.getImageName());
                    variationImages.add(imageUrl);
                    if (variation.isActive()) {
                        activeVariationCount++;
                    }
                }
                AdminProductSummaryDto productSummary = new AdminProductSummaryDto(
                        product.getId().toString(),
                        product.getName(),
                        product.getDescription(),
                        product.getBrand(),
                        product.isCancelable(),
                        product.isReturnable(),
                        null,
                        null,
                        variationImages,
                        allVariations.size(),
                        activeVariationCount,
                        product.isActive(),
                        new CategoryDetailsDto(
                                product.getCategory().getId().toString(),
                                product.getCategory().getName()
                        ),
                        product.getSeller() != null ?
                                new SellerDetailsDto(
                                        product.getSeller().getId().toString(),
                                        product.getSeller().getCompanyName()
                                ) : null
                );
                productSummaries.add(productSummary);
            }
            return new AdminProductListResponseDto(productSummaries);
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


}


