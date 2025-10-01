package com.akshay.ecommerce.dto;

import java.util.UUID;

public record CustomerProfileImageDto(
        UUID id,
        String firstName,
        String lastName,
        Boolean isActive,
        Long Contact,
        String imageUrl
){}