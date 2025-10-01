package com.akshay.ecommerce.repository;
import com.akshay.ecommerce.entity.ActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, UUID> {
    Optional<ActivationToken> findByToken(String token);
    void deleteByToken(String token);
    int deleteByCustomerId(UUID customerId);
    Optional<ActivationToken>deleteUnusedTokenByCustomerId(UUID Id);
    Optional<ActivationToken> findByCustomerIdAndUsedAtIsNull(UUID customerId);
}