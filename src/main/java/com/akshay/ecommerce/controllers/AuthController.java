package com.akshay.ecommerce.controllers;
import com.akshay.ecommerce.dto.*;
import com.akshay.ecommerce.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginRequestDto request, HttpServletResponse response) {
        return authService.login(request, response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refreshToken(HttpServletRequest request) {
        return authService.refreshToken(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        return authService.logout(request, response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgot(@Valid @RequestBody ForgotPasswordRequestDto req) {
        var errors = authService.forgotPassword(req);
        if (!errors.isEmpty()) {
            Map<String, Object> body = new HashMap<>();
            body.put("errors", errors);
            return ResponseEntity.badRequest().body(body);
        }
        return ResponseEntity.ok(new GenericMessageResponseDto(
                "A reset link has been sent to your email."
        ));
    }

    @PutMapping("/reset-password")
    public ResponseEntity<GenericMessageResponseDto> reset(@Valid @RequestBody ResetPasswordRequestDto req) {
        authService.resetPassword(req);
        return ResponseEntity.ok(new GenericMessageResponseDto("Password has been updated successfully."));
    }
}

