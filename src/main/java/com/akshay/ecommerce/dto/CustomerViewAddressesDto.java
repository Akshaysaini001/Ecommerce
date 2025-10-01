package com.akshay.ecommerce.dto;

//import com.akshay.ecommerce.Constants.AddressLabel;

import java.util.UUID;

public record CustomerViewAddressesDto(
        UUID id,
        String addressLine,
        String city,
        String state,
        String country,
        String zipCode,
String label

) {}