package com.akshay.ecommerce.entity;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
class PasswordEncoderTest {
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    @Test
    void bcrypt_shouldProduceHashWithExpectedPrefix() {
        String raw = "akshayhere";
        String hash = encoder.encode(raw);

        assertNotNull(hash);
        assertTrue(hash.startsWith("$2a") || hash.startsWith("$2b") || hash.startsWith("$2y"),
                "Hash should start with $2a/$2b/$2y for BCrypt");
    }
    @Test
    void matches_shouldVerifyRawAgainstHash() {
        String raw = "P@ssw0rd!";
        String hash = encoder.encode(raw);

        assertTrue(encoder.matches(raw, hash), "Raw password should match its BCrypt hash");
        assertNotEquals(raw, hash, "Hash must not equal raw password");
    }
}
