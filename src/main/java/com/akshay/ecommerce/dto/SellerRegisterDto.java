package com.akshay.ecommerce.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.bind.annotation.GetMapping;
@Getter
@Setter
@AllArgsConstructor
public class SellerRegisterDto {

    @NotBlank @Size(max = 30, message = "Length exceed ")
    @Pattern(regexp = "^[A-Za-z ]+$",message ="Only letters can be used and you can use space for middle name")
    private String firstName;
    @NotBlank
    @Size(max = 20, message = "Length exceed ")
    @Pattern(regexp = "^[A-Za-z]+$",message ="Only letters can be used")
    private String lastName;

    @NotBlank
    @Size(max = 254)
    @Email(message = "Invalid email format")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Email is not Valid")
    private String email;

    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,15}$",
            message = "Password must have min 8 chars and max 15 chars with atleast 1 uppercase, 1 lowercase, 1 number, and one special character"
    )
    private String password;
    @NotBlank
    private String confirmPassword;
    @NotBlank
    @Pattern(regexp = "^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z][1-9A-Z]Z[0-9A-Z]$" , message = "GSt should be valid")
    private String gst;
    @NotBlank
    @Size(max = 50, message = "Length exceed ")
    @Pattern(regexp = "^[A-Za-z][A-Za-z .&-]*$", message = "Only letters, spaces, ., -, and & are allowed")
    private String companyName;

    @Valid
    private AddressDto companyAddress;


    @NotNull
//   @Pattern(regexp = "^[6-9][0-9]{9}$") //service me direct range check ki h
    private Long companyContact;
}
