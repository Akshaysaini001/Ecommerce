package com.akshay.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResendActivationRequestDto {
    @NotEmpty
    @Size(max = 254)
    @Email
    private String email;
}
