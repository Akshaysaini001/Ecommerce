package com.akshay.ecommerce.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class SellerResponseDto {
    private String message;
    private UUID id;
}



