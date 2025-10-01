package com.akshay.ecommerce.entity;
import com.akshay.ecommerce.commonUsage.audits;
//import com.akshay.ecommerce.Constants.AddressLabel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
@Entity
@Getter
@Setter
public class Address extends audits {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String city;
    private String state;
    private String country;
    private String addressLine;
    private String zipCode;

    private String label;

//    @Enumerated(EnumType.STRING)
//    @Column(name = "label", nullable = false)
//    private AddressLabel label;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


}
