package com.akshay.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@PrimaryKeyJoinColumn(name = "user_id")
//@Table(name = "customers")
public class Customer extends User {

    @Column(nullable = false)
    private Long contact;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<ProductReview> productReviews;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Cart> carts;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> orders;
}


//
//@Getter
//@Setter
//@Entity
//public class Customer {
//
//    @Id
////    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private UUID user_id;
//    @MapsId
//    @OneToOne
//    @JoinColumn(name = "user_id")
//    private User user;
//    private String Contact;
//    @OneToMany(mappedBy = "customer")
//    private List<ProductReview> productReviews;
//    @OneToMany(mappedBy = "customer")
//    private List<Cart> carts;
//    @OneToMany(mappedBy = "customer")
//    private List<Order> orders;
//}
