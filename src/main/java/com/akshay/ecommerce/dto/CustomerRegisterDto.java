package com.akshay.ecommerce.dto;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRegisterDto {


    @NotBlank(message = "{email.notblank}")
    @Size(max = 254)
    @Email(message = "{email.invalid.format}")
    @Pattern(
            regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "{email.invalid.pattern}")
    private String email;

    @NotBlank(message = "{password.notblank}")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,15}$",
        message = "{password.invalid.pattern}"
)
private String password;
    @NotBlank(message = "{firstname.notblank}")
    @Size(max = 40, message = "{firstname.size}")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "{name.invalid.pattern}")
    private String firstName;

    @NotBlank(message = "{lastname.notblank}")
    @Size(max = 20, message = "{lastname.size}")
    @Pattern(regexp = "^[A-Za-z]+$", message = "{name.invalid.pattern}")
    private String lastName;

    @NotBlank(message = "{contact.notblank}")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "{contact.invalid.pattern}")
    private String contact;

    @NotBlank(message = "{confirmpassword.notblank}")
    private String confirmPassword;
}
