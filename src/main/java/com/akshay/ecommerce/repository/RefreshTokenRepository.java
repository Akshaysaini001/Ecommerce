package com.akshay.ecommerce.repository;
import com.akshay.ecommerce.entity.RefreshToken;
import com.akshay.ecommerce.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.UUID;
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);
    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshToken e WHERE e.user = :user")
    void deleteByUser(User user);
    @Query("select rt from RefreshToken rt join fetch rt.user where rt.token = :token")
    Optional<RefreshToken> findByTokenWithUser(String token);
    @Transactional
    @Modifying
    void deleteByToken(String token);
}
