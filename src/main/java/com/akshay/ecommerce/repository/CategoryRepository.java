package com.akshay.ecommerce.repository;
import com.akshay.ecommerce.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByNameAndParent(String name, Category parentCategory);
    List<Category> findByParentIsNull();
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);
    List<Category> findByParentAndNameIgnoreCase(Category parent, String name);
    List<Category> findByParentIsNullAndNameIgnoreCase(String name);
    @Query("SELECT c FROM Category c WHERE c.children IS EMPTY")
    List<Category> findAllLeafNodes();
    @Query("SELECT c FROM Category c WHERE c.parent IS NULL")
    List<Category> findRootNodes();
    @Query("SELECT c FROM Category c WHERE c.parent.id = :pid")
    List<Category> findChildrenOf(UUID parentId);
    @Query("SELECT COUNT(c) = 0 FROM Category c WHERE c.parent.id = :categoryId")
    boolean isLeafCategory(UUID categoryId);
    List<Category> findByParent_Id(UUID parentId);
    @Query("SELECT c FROM Category c WHERE c.children IS EMPTY")
    List<Category> findLeafNodes();
}
