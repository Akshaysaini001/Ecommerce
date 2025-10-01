package com.akshay.ecommerce.service;
import com.akshay.ecommerce.repository.RefreshTokenRepository;
import com.akshay.ecommerce.repository.UserRepository;
import com.akshay.ecommerce.entity.RefreshToken;
import com.akshay.ecommerce.entity.User;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.NotFoundException;
import com.akshay.ecommerce.exceptions.ErrorResponseDtosAndCustomExceptionClass.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    @Value("${app.jwt.refresh-expiry-minutes}")
    private long refreshTokenDurationMinutes;
    @Transactional
    public RefreshToken createRefreshToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("user not found for " + email));
        refreshTokenRepository.deleteByUser(user);
        RefreshToken rt = new RefreshToken();
        rt.setUser(user);
        rt.setToken(UUID.randomUUID().toString());
        rt.setExpiryDate(Instant.now().plusSeconds(refreshTokenDurationMinutes*60));
        log.info("Refresh token issued for {}", email);
        return refreshTokenRepository.save(rt);
    }
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByTokenWithUser(token);
    }
    public RefreshToken verifyExpiration(RefreshToken token){
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new TokenExpiredException("Refresh token expired. Please login again.");
        }
        return token;
    }
    @Transactional
    public void deleteByToken(String token){
        refreshTokenRepository.deleteByToken(token);
    }
}