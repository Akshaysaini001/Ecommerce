package com.akshay.ecommerce.repository;

import com.akshay.ecommerce.entity.Address;
import com.akshay.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {

    Optional<Address> findByIdAndUserEmail(UUID id, String email);

//    Optional<Address> findByUserId (UUID id);
List<Address> findAllByUserEmail(String email);
    boolean existsByUserAndAddressLineAndCityAndStateAndCountryAndZipCodeAndLabel(
            User user,
            String addressLine,
            String city,
            String state,
            String country,
            String zipCode,
            String label);

}