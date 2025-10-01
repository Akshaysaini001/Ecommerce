package com.akshay.ecommerce.repository;
import com.akshay.ecommerce.entity.Product;
import com.akshay.ecommerce.entity.ProductVariation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;
public interface ProductVariationRepository extends JpaRepository<ProductVariation, UUID> {
    @Query("SELECT DISTINCT v.product.brand FROM ProductVariation v WHERE v.product.category.id = :categoryId")
    List<String> findDistinctBrandsByCategory(UUID categoryId);
    @Query("SELECT MIN(v.price) FROM ProductVariation v WHERE v.product.category.id = :categoryId")
    Float findMinPriceByCategory(UUID categoryId);
    @Query("SELECT MAX(v.price) FROM ProductVariation v WHERE v.product.category.id = :categoryId")
    Float findMaxPriceByCategory(UUID categoryId);
    int countByProductAndIsActiveTrue(Product product);
    Page<ProductVariation> findByProductAndIsActiveTrue(Product product, Pageable pageable);
    Page<ProductVariation> findByProduct(Product product, Pageable pageable);

//    @Query("""
//           SELECT DISTINCT v.product.brand
//           FROM ProductVariation v
//           WHERE v.isActive = true
//             AND v.product.isActive = true
//             AND v.product.isDeleted = false
//             AND (v.product.category.id = :categoryId
//                  OR v.product.category.parent.id = :categoryId)
//           """)
}
