package com.akshay.ecommerce.entity;


import com.akshay.ecommerce.constants.fromStatusEnum;
import com.akshay.ecommerce.constants.toStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class OrderStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Enumerated(EnumType.STRING)
    private fromStatusEnum fromStatus;
    @Enumerated(EnumType.STRING)
    private toStatusEnum toStatus;
    private String transitionNotesComments;
//    private Date transitionDate;
    private LocalDateTime transitionDate;
//    LocalDateTime
    @ManyToOne
    @JoinColumn(name = "order_product_id")
    private OrderProduct orderProduct;

}
