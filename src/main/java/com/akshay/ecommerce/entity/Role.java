package com.akshay.ecommerce.entity;
import com.akshay.ecommerce.constants.Authority;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,unique = true)
    private Authority authority;
    @ManyToMany(mappedBy = "roles")
    private Set<User> user = new HashSet<>();
}
