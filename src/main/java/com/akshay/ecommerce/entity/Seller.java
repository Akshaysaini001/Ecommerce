package com.akshay.ecommerce.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@PrimaryKeyJoinColumn(name = "user_id")
//@Table(name = "sellers")
public class Seller extends User {
    private String gst;
    private Long companyContact;
    private String companyName;

    @OneToMany(mappedBy = "seller")
    private List<Product> products = new ArrayList<>();
}

//
//@Getter
//@Setter
//@Entity
//public class Seller {
//    @Id
//    private UUID user_id;
//    @OneToOne
//    @MapsId
//    @JoinColumn(name = "user_id")
//    private User user;
//    private String Gst;
//    private Long CompanyContact;
//    private String CompanyName ;
//    @OneToMany(mappedBy = "seller")
//    private List<Product> products = new ArrayList<>();
//}
