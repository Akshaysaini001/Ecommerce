package com.akshay.ecommerce.repository;
import com.akshay.ecommerce.entity.Category;
import com.akshay.ecommerce.entity.CategoryMetadataField;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryMetadataFieldRepository  extends JpaRepository<CategoryMetadataField, UUID> {
    Optional<CategoryMetadataField> findByNameIgnoreCase(String name);
    Page<CategoryMetadataField> findByNameIgnoreCase(String name, Pageable pageable);
}
