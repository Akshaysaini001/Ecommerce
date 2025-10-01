package com.akshay.ecommerce.entity;
import com.akshay.ecommerce.commonUsage.audits;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;
import java.util.UUID;
@Entity
@Setter
@Getter
@SQLDelete(sql = "UPDATE product SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false")
public class Product extends audits {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "seller_user_id")
    private Seller seller;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private String name;
    private String description;
    private boolean isCancelable;
    private boolean isReturnable;
    private String brand;
    private boolean isActive;
    private boolean isDeleted;
     @OneToMany(mappedBy = "product" , cascade = CascadeType.ALL)
     private List<ProductVariation> productVariations;
     @OneToMany(mappedBy = "product")
     private List<ProductReview>reviews;
}
