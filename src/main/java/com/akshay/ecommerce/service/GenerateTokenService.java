package com.akshay.ecommerce.service;
import com.akshay.ecommerce.entity.ActivationToken;
import com.akshay.ecommerce.repository.ActivationTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
@Service
@RequiredArgsConstructor
@Transactional
public class GenerateTokenService {
    private final ActivationTokenRepository tokenRepo;
    public ActivationToken createForCustomer(UUID customerId, Duration timetolive) {
        ActivationToken t = new ActivationToken();
        t.setToken(generateToken());
        t.setCustomerId(customerId);
        t.setExpiresAt(Instant.now().plus(timetolive));
        return tokenRepo.save(t);
    }
    private String generateToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    public UUID validate(String tokenValue) {
        ActivationToken t = tokenRepo.findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("invalid token"));
        if (t.getUsedAt() != null) throw new IllegalStateException("this token is already been used");
        if (Instant.now().isAfter(t.getExpiresAt())) throw new IllegalStateException("this token is expired");
        t.setUsedAt(Instant.now());
        return t.getCustomerId();
    }
    public ActivationToken reissueForExpired(String oldToken, Duration ttl) {
        ActivationToken old = tokenRepo.findByToken(oldToken)
                .orElseThrow(() -> new IllegalArgumentException("invalid token"));
        ActivationToken n = new ActivationToken();
        n.setCustomerId(old.getCustomerId());
        n.setToken(generateToken());
        n.setExpiresAt(Instant.now().plus(ttl));
        tokenRepo.delete(old);
        System.out.println("Remake token for customer " + n.getToken());
        return tokenRepo.save(n);
    }
//generate token and activate it all do in same code
}
