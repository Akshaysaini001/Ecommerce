package com.akshay.ecommerce.dto;
import jakarta.validation.Constraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.logging.log4j.message.Message;
@Data
public class ResetPasswordRequestDto {
    @NotBlank(message = "Token is required")
    @Size(min = 36, max = 256, message = "Invalid token length")
    private String token;

    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,15}$",
            message = "Password must have min 8 chars and max 15 chars with atleast 1 uppercase, 1 lowercase, 1 number, and one special character"
    )
    private String password;
    @NotBlank(message = "confirmPassword is required")
    private String confirmPassword;
}
