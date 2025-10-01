package com.akshay.ecommerce.dto;

import java.util.UUID;

public record SellerProfileDto(
        UUID id,
        String firstName,
        String lastName,
        Boolean isActive,
        Long companyContact,
        String companyName,
        String gst,
        String addressLine,
        String city,
        String state,
        String country,
        String zipCode
) {}