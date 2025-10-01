package com.akshay.ecommerce.repository;
import com.akshay.ecommerce.entity.Category;
import com.akshay.ecommerce.entity.Product;
import com.akshay.ecommerce.entity.Seller;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;
public interface ProductRepository extends JpaRepository<Product, UUID> {

    boolean existsByCategory(Category category);
        boolean existsByNameAndBrandAndCategoryAndSeller(String name, String brand, Category category, Seller seller);

    Page<Product> findBySellerAndIsDeletedFalse(Seller seller, Pageable pageable);
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.name = :name " +
            "AND p.brand = :brand AND p.category.id = :categoryId " +
            "AND p.seller.email = :sellerEmail AND p.id != :productId " + "AND p.isDeleted = false")
    boolean existsByNameAndBrandAndCategoryAndSellerExcludingId(
            String name, String brand, UUID categoryId, String sellerEmail, UUID productId);

    List<Product> findByCategoryAndIsActiveTrueAndIsDeletedFalse(Category category);

    List<Product> findByCategoryAndIsActiveTrueAndIsDeletedFalseAndNameContainingIgnoreCaseOrBrandContainingIgnoreCase(
            Category category, String name, String brand);

    List<Product> findByCategoryAndIsDeletedFalse(Category category);
    List<Product> findBySellerAndIsDeletedFalse(Seller seller);
    List<Product> findByIsDeletedFalse();
    List<Product> findByBrandAndIsActiveTrueAndIsDeletedFalse(String brand);
    List<Product> findByIsActiveTrueAndIsDeletedFalse();




}
