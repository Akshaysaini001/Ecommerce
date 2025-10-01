package com.akshay.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private float amountPaid;
//localdatetime is immutable so use this normal date is mutable
//    private Date createdAt;
    private LocalDateTime createdAt;
    private String paymentMethod;
    private String customerAddressCity;
    private String customerAddressState;
    private String customerAddressCountry;
    private String customerAddressAddressLine;
    private String addressZipCode;
    private String addressLabel;

    @ManyToOne
    @JoinColumn(name = "customer_user_id")
    private Customer customer;


    @OneToMany(mappedBy = "order")
    private List<OrderProduct> orderProducts;

}
