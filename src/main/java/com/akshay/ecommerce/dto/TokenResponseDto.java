package com.akshay.ecommerce.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponseDto {
    private String accessToken;
    private String tokenType = "Bearer";
    private String message;
    public TokenResponseDto(String accessToken, String message) {
        this.accessToken = accessToken;
        this.message = message;
    }
}