package com.akshay.ecommerce.repository;
import com.akshay.ecommerce.entity.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;
public interface SellerRepository extends JpaRepository<Seller, UUID> {
    boolean existsByGst(String gst);
    boolean existsByCompanyNameIgnoreCase(String companyName);
//    Optional<Seller> findByGst(String gst);
Optional<Seller> findByEmail(String email);
    Page<Seller> findByEmailContainingIgnoreCase(String email, Pageable pageable);


    @Query("SELECT s FROM Seller s LEFT JOIN FETCH s.addresses WHERE s.email = :email")
    Optional<Seller> findByEmailWithAddresses(@Param("email") String email);


    @Query("SELECT s FROM Seller s WHERE s.email = :email")
    Optional<Seller> findByUserEmail(@Param("email") String email);


}