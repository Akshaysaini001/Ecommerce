package com.akshay.ecommerce.service;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.PermissionDenied;
import com.akshay.ecommerce.repository.AddressRepository;
import com.akshay.ecommerce.repository.CategoryRepository;
import com.akshay.ecommerce.repository.SellerRepository;
import com.akshay.ecommerce.repository.UserRepository;
import com.akshay.ecommerce.dto.*;
import com.akshay.ecommerce.dto.viewCategory.AssociatedMetadataDto;
import com.akshay.ecommerce.dto.viewCategory.CategoryBriefDto;
import com.akshay.ecommerce.dto.viewCategory.CategoryDetailDto;
import com.akshay.ecommerce.entity.*;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.NotFoundException;
import com.akshay.ecommerce.exceptions.PasswordMismatchException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
@Service
@AllArgsConstructor
@Slf4j
public class SellerProfileService {
    @Autowired
    private SellerRepository sellerRepository;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;
    private final AddressRepository addressRepository;
    private final CategoryRepository categoryRepo;
//for categories endpoints  as a seller
    @Transactional
    public List<CategoryDetailDto> getCategories() {
        List<Category> leafCategories = categoryRepo.findAllLeafNodes();
        List<CategoryDetailDto> dtoList = new ArrayList<>();
        for (Category cat : leafCategories) {
            CategoryDetailDto dto = new CategoryDetailDto();
            dto.setId(cat.getId());
            dto.setName(cat.getName());
            List<CategoryBriefDto> parentPath = new ArrayList<>();
            Category p = cat.getParent();
            while (p != null) {
                CategoryBriefDto brief = new CategoryBriefDto();
                brief.setId(p.getId());
                brief.setName(p.getName());
                parentPath.add(brief);
                p = p.getParent();
            }
            Collections.reverse(parentPath);
            dto.setParentPath(parentPath);
            dto.setChildren(
                    cat.getChildren().stream()
                            .map(child -> {
                                CategoryBriefDto cBrief = new CategoryBriefDto();
                                cBrief.setId(child.getId());
                                cBrief.setName(child.getName());
                                return cBrief;
                            })
                            .collect(Collectors.toList())
            );
            List<AssociatedMetadataDto> metaDtos =
                    cat.getMetadataFieldValues().stream()
                            .map(this::mapToMetaDto)
                            .collect(Collectors.toList());
            dto.setAssociatedMetadata(metaDtos);
            dtoList.add(dto);
        }
        return dtoList;
    }
    private AssociatedMetadataDto mapToMetaDto(CategoryMetadataFieldValues fv) {
        AssociatedMetadataDto dto = new AssociatedMetadataDto();
        dto.setFieldId(fv.getCategoryMetadataField().getId());
        dto.setFieldName(fv.getCategoryMetadataField().getName());
        dto.setPossibleValues(fv.getValues());
        return dto;
    }
    @Transactional
    public sellerProfileImageDto viewMyProfile(Authentication authentication) {
        String imageUrl = "/profile/image1.jpeg";
        String email = authentication.getName();
        Seller seller = findSellerByEmailOrThrow(email);
        Address addr = seller.getAddresses().get(0);
        return new sellerProfileImageDto(seller.getId(), seller.getFirstName(), seller.getLastName(),
                seller.isActive(), seller.getCompanyContact(), seller.getCompanyName(),
                seller.getGst(), addr.getAddressLine(), addr.getCity(),
                addr.getState(), addr.getCountry(), addr.getZipCode(),imageUrl
        );
    }
    Seller findSellerByEmailOrThrow(String email) {
        return sellerRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Seller is not found"));
    }
    @Transactional
    public SellerProfileDto updateProfile(Authentication authentication, SellerProfileUpdateDto req) {
        String email = authentication.getName();
        Seller seller = sellerRepository.findByEmailWithAddresses(email).orElseThrow(() -> new NotFoundException("Seller not found"));
        seller.setFirstName(req.firstName());
        seller.setLastName(req.lastName());
        seller.setActive(req.isActive());
        seller.setCompanyName(req.companyName());
        seller.setCompanyContact(Long.valueOf(req.companyContact()));
        seller.setGst(req.gst());
        Address address = seller.getAddresses().get(0);
        AddressUpdateDto a = req.address();
        address.setAddressLine(a.addressLine());
        address.setCity(a.city());
        address.setState(a.state());
        address.setCountry(a.country());
        address.setZipCode(a.zipCode());
        Seller saved = sellerRepository.save(seller);
        Address addr = saved.getAddresses().get(0);
        return new SellerProfileDto(
                saved.getId(), saved.getFirstName(), saved.getLastName(), saved.isActive(),
                saved.getCompanyContact(), saved.getCompanyName(), saved.getGst(),
                addr.getAddressLine(), addr.getCity(),
                addr.getState(), addr.getCountry(),
                addr.getZipCode()
        );
    }
    @Transactional
    public void changePassword(Authentication auth, ChangePasswordRequestDto req) {
        String password = req.password();
        String confirm = req.confirmPassword();
        if (password == null || confirm == null || !password.equals(confirm)) {
            throw new PasswordMismatchException("Passwords do not match");
        }
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordUpdateDate(LocalDateTime.now());
        userRepository.save(user);
        emailService.sendPasswordChangeEmail(email);
    }



//    @Transactional
//    public void updateAddress(Authentication auth, UUID id, AddressUpdateDto req) {
//        String email = auth.getName();
////
////        Address addr = addressRepository.findById(id)
////                .orElseThrow(() -> new NotFoundException("Address not found"));
//
//        Address addr = addressRepository.findByIdAndUserEmail(id, email)
//                .orElseThrow(() -> new NotFoundException("Address not found"));
//        addr.setCity(req.city());
//        addr.setState(req.state());
//        addr.setCountry(req.country());
//        addr.setAddressLine(req.addressLine());
//        addr.setZipCode(req.zipCode());
//        addressRepository.save(addr);
//    }

@Transactional
public void updateAddress(Authentication auth, UUID id, AddressUpdateDto req) {
    Address addr = addressRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Address not found"));
    if (!auth.getName().equalsIgnoreCase(addr.getUser().getEmail())) {
        throw new PermissionDenied("You are not authorized to update this address");
    }
    addr.setCity(req.city());
    addr.setState(req.state());
    addr.setCountry(req.country());
    addr.setAddressLine(req.addressLine());
    addr.setZipCode(req.zipCode());
    addressRepository.save(addr);
}

}