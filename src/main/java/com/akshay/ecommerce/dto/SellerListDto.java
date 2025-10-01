package com.akshay.ecommerce.dto;

import java.util.UUID;

public record SellerListDto(
        UUID id,
        String fullName,
        String email,
        Boolean isActive,
        String companyName,
        String companyAddress,
        Long companyContact
) {}