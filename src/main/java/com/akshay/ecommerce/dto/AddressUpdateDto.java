package com.akshay.ecommerce.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.*;
public record AddressUpdateDto(
        @NotBlank(message = "cant be blank")
        @Size(min = 2, max = 64  , message = "size invalid 2 to 64 character allowed")
        @Pattern(regexp = "^[A-Za-z ]+$", message = "City must contain only letters and spaces")
        String city,

        @NotBlank(message = "cant be blank")
        @Size(min = 2, max = 64 , message = "size invalid 2 to 64 character allowed")
        @Pattern(regexp = "^[A-Za-z ]+$", message = "State must contain only letters and spaces")
        String state,

        @NotBlank(message = "cant be blank")
        @Size(min = 2, max = 64 , message = "size invalid 2 to 64 character allowed")
        @Pattern(regexp = "^[A-Za-z ]+$", message = "Country must contain only letters and space")
        String country,

        @NotBlank(message = "cant be blank")
        @Size(min = 2, max = 200 , message = "size invalid 2 to 200 character allowed" )
//        @Pattern(regexp = "^[A-Za-z0-9 ,.#\\-/]+$", message = "characters in address line are invalid")
        String addressLine,

        @NotBlank(message = "cant be blank")
        @Pattern(regexp = "^[1-9][0-9]{5}$", message = "PIN must be a 6‑digit and can not start with 0")
        String zipCode
) {}



