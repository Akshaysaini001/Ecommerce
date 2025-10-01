package com.akshay.ecommerce.dto;

import java.util.UUID;

public record CustomerProfileDto(
        UUID id,
        String firstName,
        String lastName,
        Boolean isActive,
        Long Contact
){}

