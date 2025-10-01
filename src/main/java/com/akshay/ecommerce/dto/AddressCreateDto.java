package com.akshay.ecommerce.dto;
//import com.akshay.ecommerce.Constants.AddressLabel;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressCreateDto(
        @NotBlank(message = "can not be empty")
        @Size(min = 2, max = 64 , message = "minimum length is 2 and max is 64 for city")
        @Pattern(regexp = "^[A-Za-z ]+$", message = "City must contain only letters and spaces")
        String city,

        @NotBlank(message = "can not be empty")
        @Size(min = 2, max = 64 , message = "minimum length is 2 and max is 64 for state")
        @Pattern(regexp = "^[A-Za-z ]+$", message = "State must contain only letters and spaces")
        String state,

        @NotBlank(message = "can not be empty")
        @Size(min = 2, max = 64 , message = "minimum length is 2 and max is 64 for country")
        @Pattern(regexp = "^[A-Za-z ]+$", message = "Country must contain only letters and spaces")
        String country,

        @NotBlank(message = "can not be empty")
        @Size(min = 2, max = 200 , message = "minimum length is 2 and max is 64 for addressLine")
        @Pattern(regexp = "^[A-Za-z0-9 ,.#\\-/]+$", message = "characters in address line are invalid")
        String addressLine,

        @NotBlank
        @Pattern(regexp = "^[1-9][0-9]{5}$", message = "PIN must be a 6‑digit")
        String zipCode,

        @NotBlank
        @Pattern(regexp = "^(HOME|OFFICE|OTHER)$", message = "Label must be either HOME, OFFICE or OTHER")
        String label

) {}
