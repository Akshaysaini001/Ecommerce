package com.akshay.ecommerce.repository;
import com.akshay.ecommerce.entity.Category;
import com.akshay.ecommerce.entity.CategoryMetadataField;
import com.akshay.ecommerce.entity.CategoryMetadataFieldValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface CategoryMetadataFieldValuesRepository extends JpaRepository<CategoryMetadataFieldValues, UUID> {
    Optional<CategoryMetadataFieldValues> findByCategoryAndCategoryMetadataField(Category category, CategoryMetadataField categoryMetadataField);
    boolean existsByCategoryAndCategoryMetadataField(
            Category category, CategoryMetadataField categoryMetadataField);


    @Query("SELECT cmfv FROM CategoryMetadataFieldValues cmfv WHERE cmfv.category.id = :categoryId")
    List<CategoryMetadataFieldValues> findByCategoryId(@Param("categoryId") UUID categoryId);
}
