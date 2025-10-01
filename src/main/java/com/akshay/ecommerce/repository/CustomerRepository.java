package com.akshay.ecommerce.repository;

import com.akshay.ecommerce.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    boolean existsByEmail(String email);
    boolean existsByContact(Long contact);
    Optional<Customer> findByEmail(String email);

    Page<Customer> findByEmailContainingIgnoreCase(String email, Pageable pageable);
}
