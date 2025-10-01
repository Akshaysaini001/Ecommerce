package com.akshay.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "customer_user_id")
    private Customer customer;

    private long quantity;
    private boolean isWishListItem = false;

    @ManyToOne
    @JoinColumn(name = "product_variation_id")
    private ProductVariation productVariation;


}
