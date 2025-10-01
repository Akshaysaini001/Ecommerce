package com.akshay.ecommerce.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Instant;
import java.util.UUID;
@Entity
@Getter @Setter
public class ActivationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
//    @NotBlank(message = "token is required")
    private String token;
    private UUID customerId;
    private Instant expiresAt;
    private Instant usedAt;
}
