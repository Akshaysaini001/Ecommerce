package com.akshay.ecommerce.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequestDto {
    @Email(message = "{login.email.invalid}")
    @NotBlank(message = "{login.email.required}")
    @Size(max = 254, message = "{login.email.size}")
    private String email;
    @NotBlank(message = "{login.password.required}")
    @Size(min = 8, max = 15, message = "{login.password.size}")
    private String password;

}
