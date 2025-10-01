package com.akshay.ecommerce.service;


import com.akshay.ecommerce.exceptions.DuplicateException;
import com.akshay.ecommerce.repository.*;
import com.akshay.ecommerce.dto.*;
import com.akshay.ecommerce.dto.viewCategory.AssociatedMetadataDto;
import com.akshay.ecommerce.dto.viewCategory.CategoryBriefDto;
import com.akshay.ecommerce.entity.*;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.NotFoundException;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.PermissionDenied;
import com.akshay.ecommerce.exceptions.PasswordMismatchException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class CustomerProfileService {
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;
    private final EmailService emailService;
    private final CategoryRepository categoryRepo;
    private final ProductVariationRepository productVariationRepository;

    @Transactional
    public CustomerProfileImageDto viewCustomerProfile(Authentication authentication) {
        String imageUrl = "/profile/image2.jpeg";

        String email = authentication.getName();
        Customer customer = findCustomerByEmailOrThrow(email);
        return new CustomerProfileImageDto(customer.getId(), customer.getFirstName(), customer.getLastName(),
                customer.isActive(), customer.getContact(),imageUrl
        );
    }
    private Customer findCustomerByEmailOrThrow(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    @Transactional
    public List<CustomerViewAddressesDto> getAddresses(Authentication authentication) {
        String email = authentication.getName();
        List<Address> addresses = addressRepository.findAllByUserEmail(email);
        return addresses.stream()
                .map(address -> new CustomerViewAddressesDto(address.getId(), address.getAddressLine(),
                        address.getCity(), address.getState(),
                        address.getCountry(), address.getZipCode(), address.getLabel()
                ))
                .toList();
    }

    @Transactional
    public void updateProfile(Authentication authentication, CustomerProfileUpdateDto req) {
        String email = authentication.getName();
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
        if (req.firstName() != null) {
            customer.setFirstName(req.firstName());
        }
        if (req.lastName() != null) {
            customer.setLastName(req.lastName());
        }
        if (req.middleName() != null) {
            customer.setMiddleName(req.middleName());
        }
        if (req.contact() != null) {
            customer.setContact(Long.parseLong(req.contact()));
        }
        customerRepository.save(customer);
    }


    @Transactional
    public void changePassword(Authentication auth, ChangePasswordRequestDto req) {
        String password = req.password();
        String confirm = req.confirmPassword();
        if (!password.equals(confirm)) {
            throw new PasswordMismatchException("Passwords do not match");
        }
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        emailService.sendPasswordChangeEmail(email);
    }

    @Transactional
    public void addAddress(Authentication authentication, AddressCreateDto req) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found."));

        boolean addressExists = addressRepository.existsByUserAndAddressLineAndCityAndStateAndCountryAndZipCodeAndLabel(user,
                req.addressLine(), req.city(), req.state(), req.country(), req.zipCode(), req.label()
        );

        if (addressExists) {
            throw new DuplicateException("This address already exists for the user.");
        }

        Address address = new Address();
        address.setAddressLine(req.addressLine());
        address.setCity(req.city());
        address.setState(req.state());
        address.setCountry(req.country());
        address.setZipCode(req.zipCode());
        address.setLabel(req.label());
        address.setUser(user);
        addressRepository.save(address);
    }

//    @Transactional
//    public void addAddress(Authentication authentication, AddressCreateDto req) {
//        String email = authentication.getName();
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new NotFoundException("User not found."));
//        Address Address = new Address();
//        Address.setAddressLine(req.addressLine());
//        Address.setCity(req.city());
//        Address.setState(req.state());
//        Address.setCountry(req.country());
//        Address.setZipCode(req.zipCode());
//        Address.setLabel(req.label());
//        Address.setUser(user);
//        addressRepository.save(Address);
//    }

    @Transactional
    public void deleteAddress(Authentication authentication, UUID addressId) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found."));
          Address address = addressRepository.findById(addressId)
                  .orElseThrow(() -> new NotFoundException("Address not found"));
          if(address.getUser().getId().equals(user.getId())) {
              addressRepository.delete(address);
          }
          else {
              throw new PermissionDenied("You do not have permission to delete this address.");
          }
    }

    @Transactional
    public void updateAddress(Authentication authentication, UUID addressId, AddressCreateDto req) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found."));
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new NotFoundException("Address not found"));

        if (!address.getUser().getId().equals(user.getId())) {
            throw new PermissionDenied("You do not have permission to update this address.");
        }
        address.setAddressLine(req.addressLine());
        address.setCity(req.city());
        address.setState(req.state());
        address.setCountry(req.country());
        address.setZipCode(req.zipCode());
        address.setLabel(req.label());
    }



    public List<CategoryBriefDto> listCategories() {
        List<Category> categories = categoryRepo.findLeafNodes();
        List<CategoryBriefDto> dtoList = new ArrayList<>();
        for (Category c : categories) {
            CategoryBriefDto dto = new CategoryBriefDto();
            dto.setId(c.getId());
            dto.setName(c.getName());
            dtoList.add(dto);
        }
        return dtoList;
    }



//        public List<CategoryBriefDto> listCategories(UUID parentId) {
//            List<Category> categories;
//            if (parentId == null) {
//                categories = categoryRepo.findRootNodes();
//            } else {
//                Category parent = categoryRepo.findById(parentId)
//                        .orElseThrow(() -> new NotFoundException("Category not found"));
//                categories = categoryRepo.findChildrenOf(parent.getId());
//            }
//            List<CategoryBriefDto> dtoList = new ArrayList<>();
//            for (Category c : categories) {
//                CategoryBriefDto dto = new CategoryBriefDto();
//                dto.setId(c.getId());
//                dto.setName(c.getName());
//                dtoList.add(dto);
//            }
//            return dtoList;
//        }

    @Transactional
    public CategoryFilterDto customerFilterDetailForCategory (UUID categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        List<AssociatedMetadataDto> metadataFields = category.getMetadataFieldValues()
                .stream()
                .map(this::toAssociatedMetadataDto)
                .toList();
        List<String> brands =productVariationRepository.findDistinctBrandsByCategory(categoryId);
        Float minPrice = productVariationRepository.findMinPriceByCategory(categoryId);
        Float maxPrice = productVariationRepository.findMaxPriceByCategory(categoryId);

        return new CategoryFilterDto(metadataFields, brands, minPrice, maxPrice);
    }
    private AssociatedMetadataDto toAssociatedMetadataDto(CategoryMetadataFieldValues fieldValues) {
        CategoryMetadataField field = fieldValues.getCategoryMetadataField();
        return new AssociatedMetadataDto(
                field.getId(),
                field.getName(),
                fieldValues.getValues()
        );
    }






    }
// admin can see categories everything from parent to child , as well as metadata
// seller will see leaf where product listing will be done
// customer will se only part that is root node same parent same level all categories





