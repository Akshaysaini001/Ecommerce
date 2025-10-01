package com.akshay.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressDto {
    @NotBlank(message = "City is required")
    @Size(min = 2, max = 64, message = "City must be less than 64 characters and minimum 2 ")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "City can contain only characters and spaces")
    private String city;

    @NotBlank(message = "State is required")
    @Size(min = 2, max = 64, message = "State must be less than 64 characters and more than 1 character")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "State can contain only characters and spaces")
    private String state;

    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 64, message = "Country must be less than 64 characters and minimum 2 ")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Country can contain only characters and spaces")
    private String country;

    @NotBlank(message = "Address line is required")
    @Size(min = 2, max = 128, message = "AddressLine must be between 2 and 128 characters")
    private String addressLine;

    @NotBlank(message = "Zip code is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Zip code can contain only digits")
    private String zipCode;

}