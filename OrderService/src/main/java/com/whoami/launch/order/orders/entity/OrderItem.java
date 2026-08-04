package com.whoami.launch.order.orders.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;
    private String productId;

    @Column(length = 1024)
    private String productNameSnapshot;

    @Column(length = 2048)
    private String imageSnapshot;

    private BigDecimal priceSnapshot;
    private Integer quantity;
    private BigDecimal totalPrice;

   
}
