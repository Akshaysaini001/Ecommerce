package com.akshay.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
public class CustomerRegisterResponseDto {
    private String message;
    private UUID id;
}
