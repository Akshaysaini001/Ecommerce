package com.akshay.ecommerce.service;

//import com.akshay.ecommerce.Constants.AddressLabel;
import com.akshay.ecommerce.constants.Authority;
import com.akshay.ecommerce.repository.AddressRepository;
import com.akshay.ecommerce.repository.RoleRepository;
import com.akshay.ecommerce.repository.SellerRepository;
import com.akshay.ecommerce.repository.UserRepository;
import com.akshay.ecommerce.dto.AddressDto;
import com.akshay.ecommerce.dto.SellerRegisterDto;
import com.akshay.ecommerce.entity.Address;
import com.akshay.ecommerce.entity.Role;
import com.akshay.ecommerce.entity.Seller;
import com.akshay.ecommerce.entity.User;
import com.akshay.ecommerce.exceptions.DuplicateException;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.InvalidContactNumerException;

import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.InvalidGSTNumerException;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.RequiredFieldMissingException;
import com.akshay.ecommerce.exceptions.PasswordMismatchException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SellerService {
    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final AddressRepository addressRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Transactional
    public UUID register(SellerRegisterDto dto) {
        if (dto.getEmail() == null) throw new RequiredFieldMissingException("Email is required");
        String email = dto.getEmail().trim().toLowerCase();
        if (dto.getGst() == null) throw new RequiredFieldMissingException("GST is required");
        String gst = dto.getGst().trim().toUpperCase();
        if (dto.getCompanyName() == null) throw new RequiredFieldMissingException("Company name is required");
        String companyName = dto.getCompanyName().trim();
        if (dto.getFirstName() == null) throw new RequiredFieldMissingException("First name is required");
        String firstName = dto.getFirstName().trim();
        if (dto.getLastName() == null) throw new RequiredFieldMissingException("Last name is required");
        String lastName = dto.getLastName().trim();
        AddressDto addrDto = dto.getCompanyAddress();
        if (addrDto == null) throw new RequiredFieldMissingException("Company address is required");
        if (addrDto.getCity() == null || addrDto.getState() == null ||
                addrDto.getCountry() == null || addrDto.getAddressLine() == null ||
                addrDto.getZipCode() == null) {
            throw new RequiredFieldMissingException("Company address is incomplete");
        }
        String city = addrDto.getCity().trim();
        String state = addrDto.getState().trim();
        String country = addrDto.getCountry().trim();
        String addressLine = addrDto.getAddressLine().trim();
        String zip = addrDto.getZipCode().trim();

        if (userRepository.existsByEmail(email)) throw new DuplicateException("Email already exists");
        if (sellerRepository.existsByGst(gst)) throw new DuplicateException("GST already exists");
        if (sellerRepository.existsByCompanyNameIgnoreCase(companyName)) throw new DuplicateException("Company already in use");
        if (dto.getPassword() == null || dto.getConfirmPassword() == null ||
                !dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new PasswordMismatchException("Passwords do not match");
        }
        Long contact = dto.getCompanyContact();
        if (contact == null || contact < 6000000000L || contact > 9999999999L) {
            throw new InvalidContactNumerException("Company contact is not valid");
        }
        if (!gst.matches("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][1-9A-Z]Z[0-9A-Z]$")) {
            throw new InvalidGSTNumerException("GST is not valid");
        }
        Seller seller = new Seller();
        seller.setEmail(email);
        seller.setGst(gst);
        seller.setCompanyName(companyName);
        seller.setFirstName(firstName);
        seller.setLastName(lastName);
        seller.setCompanyContact(contact);
        seller.setPassword(passwordEncoder.encode(dto.getPassword()));
        Address address = new Address();
        address.setCity(city);
        address.setState(state);
        address.setCountry(country);
        address.setAddressLine(addressLine);
        address.setZipCode(zip);
        address.setLabel("COMPANY");
//        address.setLabel(AddressLabel.valueOf("COMPANY"));
        address.setUser((User) seller);
//        seller.setAddresses(List.of(address));
        if (seller.getAddresses() == null) {
            seller.setAddresses(new java.util.ArrayList<>());
        }
        seller.getAddresses().add(address);
        Role sellerRole = roleRepository.findByAuthority(Authority.SELLER)
                .orElseGet(() -> {
                    Role r = new Role();
                    r.setAuthority(Authority.SELLER);
                    return roleRepository.save(r);
                });
        seller.addRole(sellerRole);
        seller = sellerRepository.save(seller);
        try {
            emailService.sendRegisterEmail(seller.getEmail(), seller.getCompanyName());
        } catch (Exception e) {
            log.warn("Seller onboarding email failed for {}: {}", seller.getEmail(), e.toString(), e);
        }
        return seller.getId();
    }


}

//    public UUID register(SellerRegisterDto sellerRegisterDto) {
//        //unique validations
//        if(userRepository.existsByEmail(sellerRegisterDto.getEmail())) {
//            throw new DuplicateException("Email already exists");
//        }
//        if(sellerRepository.existsByGst(sellerRegisterDto.getGst())) {
//            throw new DuplicateException("GST already exists");
//        }
//        if (sellerRepository.existsByCompanyNameIgnoreCase(sellerRegisterDto.getCompanyName())) {
//            throw new DuplicateException("Company already in use");
//        }
//        //phonenumber,password,gst
//        Long contact = sellerRegisterDto.getCompanyContact();
//        if (contact == null || contact < 6000000000L || contact > 9999999999L) {
//            throw new InvalidContactNumerException("Company contact is not valid");
//        }
//        if(!sellerRegisterDto.getPassword().equals(sellerRegisterDto.getConfirmPassword())) {
//            throw new PasswordMismatchException("Passwords not matches");
//        }
//        if(!(sellerRegisterDto.getGst()).matches("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][1-9A-Z]Z[0-9A-Z]$")){
//            throw new InvalidGSTNumerException("GST is not valid");
//        }
//        Seller seller = new Seller();
//        seller.setEmail(sellerRegisterDto.getEmail());
//        seller.setGst(sellerRegisterDto.getGst());
//        seller.setCompanyName(sellerRegisterDto.getCompanyName());
//        seller.setPassword(sellerRegisterDto.getPassword());
//        seller.setFirstName(sellerRegisterDto.getFirstName());
//        seller.setLastName(sellerRegisterDto.getLastName());
//        seller.setCompanyContact(sellerRegisterDto.getCompanyContact());
//        sellerRepository.save(seller);
//        try {
//            emailService.sendRegisterEmail(seller.getEmail(), seller.getCompanyName());
//        } catch (Exception e) {
//            System.err.println("Seller onboarding email failed for " + seller.getEmail() + ": " + e.getMessage());
//        }
//        return seller.getId();
//    }
//}
