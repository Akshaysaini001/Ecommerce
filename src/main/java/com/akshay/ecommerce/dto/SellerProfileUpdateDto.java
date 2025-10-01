package com.akshay.ecommerce.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
public record SellerProfileUpdateDto(
        @NotBlank @Size(max = 50)
        @Pattern(regexp = "^[A-Za-z ]+$", message = "Only letters allowed and space also allowed")
        String firstName,
        @Pattern(regexp = "^[A-Za-z]+$", message = "Only letters allowed")
        @NotBlank @Size(max = 50 ,message = "Size limit is 50 characters")
        String lastName,
        @NotNull(message = "this is mandatory field")
        Boolean isActive,

        @NotNull(message = "Company contact cannot be null")
        @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Enter 10-digit Indian mobile number starting with 6,7,8, or 9")
        String companyContact,


                @NotBlank(message = "Company name cannot be blank")
        @Size(max = 120, message = "Company name can have max 120 characters")
        String companyName,

        @Pattern(
                regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
                message = "Invalid GST format"
        )
        @NotBlank(message = "Gst can not be blank ")
        @Size(min = 15, max = 15, message = "GST number must be exactly 15 characters")
        @Pattern(
                regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$",
                message = "Invalid GST format"
        )
        String gst,

        @NotNull(message = "Address cannot be null")
        @Valid
        AddressUpdateDto address
) { }