package com.akshay.ecommerce.dto;
import java.util.UUID;
public record CustomerListDto(
        UUID id,
        String fullName,
        String email,
        Boolean isActive
) {}
