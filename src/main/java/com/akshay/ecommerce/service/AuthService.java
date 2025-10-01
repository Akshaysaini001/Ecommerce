package com.akshay.ecommerce.service;
import com.akshay.ecommerce.repository.ActivationTokenRepository;
import com.akshay.ecommerce.repository.UserRepository;
import com.akshay.ecommerce.dto.ForgotPasswordRequestDto;
import com.akshay.ecommerce.dto.LoginRequestDto;
import com.akshay.ecommerce.dto.ResetPasswordRequestDto;
import com.akshay.ecommerce.dto.TokenResponseDto;
import com.akshay.ecommerce.entity.RefreshToken;
import com.akshay.ecommerce.entity.User;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.InvalidTokenException;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.NotFoundException;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.TokenExpiredException;
import com.akshay.ecommerce.exceptions.PasswordMismatchException;
import com.akshay.ecommerce.security.Caffeine;
import com.akshay.ecommerce.security.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Duration RESET_TTL = Duration.ofMinutes(15);
    private static final String FRONTEND_RESET_URL = "http://localhost:5173/reset?token=%s"; // adjust if needed
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final GenerateTokenService generateTokenService;
    private final JpaUserDetailsService userDetailsService;
    private final EmailService emailService;
    private final AccountLockoutService lockoutService;
    private final UserRepository userRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final Caffeine blacklistService;
    private final RefreshTokenService refreshTokenService;
    private final MessageService messageService;
    public ResponseEntity<Map<String, Object>> login(LoginRequestDto request, HttpServletResponse response) {
        String email = request.getEmail();
        String password = request.getPassword();
        try {
            if (lockoutService.isAccountLocked(email)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", messageService.getMessage("login.account.locked")));
            }
            var userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", messageService.getMessage("login.email.notexist")));
            }
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            var userDetails = userDetailsService.loadUserByUsername(email);
            String accessToken = jwtService.generateToken(userDetails);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(email);
            setRefreshTokenCookie(response, refreshToken.getToken());
            lockoutService.resetFailedAttempts(email);
            return ResponseEntity.ok(Map.of(
                    "message", messageService.getMessage("login.success"),
                    "accessToken", accessToken,
                    "tokenType", "Bearer"
            ));
        } catch (BadCredentialsException e) {
            lockoutService.recordFailedAttempt(email);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", messageService.getMessage("login.incorrect.password")));
        } catch (AuthenticationException e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : messageService.getMessage("login.authentication.failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", errorMsg));
        }
    }
    public ResponseEntity<TokenResponseDto> refreshToken(HttpServletRequest request) {
        String refreshToken = getRefreshTokenFromCookie(request);
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new TokenResponseDto(null, "Refresh token is missing"));
        }
        try {
            Optional<RefreshToken> opt = refreshTokenService.findByToken(refreshToken);
            if (opt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new TokenResponseDto(null, "Invalid refresh token"));
            }
            RefreshToken valid = refreshTokenService.verifyExpiration(opt.get());
            User user = valid.getUser();
            if (user.isLocked()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new TokenResponseDto(null, "User account is locked"));
            }
            var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
            String newAccessToken = jwtService.generateToken(userDetails);
            return ResponseEntity.ok(new TokenResponseDto(newAccessToken, "Token refreshed successfully"));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new TokenResponseDto(null, ex.getMessage()));
        }
    }

    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String jti = jwtService.extractJti(token);
                Date expiration = jwtService.extractExpiration(token);
                long remainingTime = expiration.getTime() - System.currentTimeMillis();
                if (remainingTime > 0) {
                    blacklistService.blacklist(jti, Duration.ofMillis(remainingTime));
                }
            }catch(Exception e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", e.getMessage()));
            }
        }
        String refreshToken = getRefreshTokenFromCookie(request);
        if (refreshToken != null) {
            try {
                refreshTokenService.deleteByToken(refreshToken);
            } catch (Exception e) {
                log.warn("Unable to findthe refresh token: {}", e.getMessage());
            }
        }
        clearRefreshTokenCookie(response);
        return ResponseEntity.ok(Map.of("message", "Logout successfully"));
    }
    private void setRefreshTokenCookie(HttpServletResponse response, String value) {
        Cookie cookie = new Cookie("refreshToken", value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60);
        response.addCookie(cookie);
    }
    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
    private String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies()) {
            if ("refreshToken".equals(c.getName())) return c.getValue();
        }
        return null;
    }
    @Transactional
    public List<String> forgotPassword(ForgotPasswordRequestDto request) {
        var errors = new ArrayList<String>();
        String email = request.getEmail();
        var userOpt = userRepository.findByEmail(email); // or findByEmailIgnoreCase
        if (userOpt.isEmpty()) {
            errors.add("email not found");
            return errors;
        }
        var user = userOpt.get();
        if (!user.isActive()) {
            errors.add("account is not activated");
            return errors;
        }
        activationTokenRepository.deleteByCustomerId(user.getId());
        var tokenEntity = generateTokenService.createForCustomer(user.getId(), Duration.ofMinutes(15));
        String token = tokenEntity.getToken();
        String link = String.format("http://localhost:8080/reset?token=%s", token);
        log.info("Reset Token is: {}", token);
        try {
            emailService.sendPasswordResetEmail(user.getEmail(), link, (int) Duration.ofMinutes(15).toMinutes());
        } catch (Exception e) {
          log.warn("Unable to send password reset email: {}", e.getMessage());
        }
        return List.of();
    }


    @Transactional
    public void resetPassword(ResetPasswordRequestDto request) {
        // 1) Field/business validation
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException("password and confirm password must match");
        }
        var token = activationTokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new InvalidTokenException("invalid token"));

//        if (token.getUsedAt() != null) {
//            throw new TokenAlreadyUsedException("token is already used");
//        }
        if (token.getExpiresAt() != null && token.getExpiresAt().isBefore(Instant.now())) {
            throw new TokenExpiredException("token is expired");
        }
        var user = userRepository.findById(token.getCustomerId())
                .orElseThrow(() -> new NotFoundException("user not found for token"));
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        activationTokenRepository.delete(token);
        emailService.sendPlainEmail(user.getEmail(),"Your password is changed", "IF not you contact suppport");
    }


//
//    public ResponseEntity<Map<String, Object>> login(LoginRequestDto request) {
//        String email = request.getEmail();
//        String password = request.getPassword();
//
//        try {
//            if (lockoutService.isAccountLocked(email)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(Map.of("message", "Account is locked due to multiple fail attempts"));
//            }
//            var userOpt = userRepository.findByEmail(email);
//            if (userOpt.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(Map.of("message", "Email does not exist"));
//            }
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(email, password)
//            );
//            var userDetails = userDetailsService.loadUserByUsername(email);
//            String token = jwtService.generateToken(userDetails);
//            return ResponseEntity.ok(Map.of(
//                    "message", "Login successful",
//                    "token", token
//            ));
//        } catch (BadCredentialsException e) {
//            lockoutService.recordFailedAttempt(email);
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("message", "Incorrect password"));
//        } catch (AuthenticationException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(Map.of("message", e.getMessage() != null ? e.getMessage() : "Authentication failed"));
//        }
//    }
//    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            String token = authHeader.substring(7);
//            try {
//                String jti = jwtService.extractJti(token);
//                Date expiration = jwtService.extractExpiration(token);
//                long remainingTime = expiration.getTime() - System.currentTimeMillis();
//                if (remainingTime > 0) {
//                    Duration ttl = Duration.ofMillis(remainingTime);
//                    blacklistService.blacklist(jti, ttl);
//                }
//            } catch (Exception e) {
//                log.warn("Invalid token during logout: {}", e.getMessage());
//            }
//        }
//        return ResponseEntity.ok(Map.of("message", "logout done"));
//    }
}