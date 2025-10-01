package com.akshay.ecommerce.repository;

import com.akshay.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    @Query("select e from User e left join fetch e.roles where e.email =:email")
    Optional<User> findByEmailWithRoles( String email);

    List<User> isLocked(boolean isLocked);

List<User> findByIsLocked(boolean isLocked);




}
