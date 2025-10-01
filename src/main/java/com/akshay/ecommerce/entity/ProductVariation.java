package com.akshay.ecommerce.entity;
import com.fasterxml.jackson.annotation.JsonTypeId;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;
@Entity
@Getter
@Setter
public class ProductVariation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private long quantityAvailable;
    private float price;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSON")
    private JsonNode metadata;
    private String imageName;
    private boolean isActive;
    @OneToMany(mappedBy = "productVariation")
    private List<Cart> carts;
    @OneToMany(mappedBy = "productVariation")
    private List<OrderProduct> orderProducts;
}
