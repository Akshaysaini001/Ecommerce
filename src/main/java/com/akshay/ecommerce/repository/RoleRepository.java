package com.akshay.ecommerce.repository;

import com.akshay.ecommerce.constants.Authority;
import com.akshay.ecommerce.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    Optional<Role> findByAuthority(Authority authority);
}