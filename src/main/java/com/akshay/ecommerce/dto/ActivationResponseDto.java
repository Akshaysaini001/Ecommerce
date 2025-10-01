package com.akshay.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public record ActivationResponseDto(String message, UUID customerId) {

}
