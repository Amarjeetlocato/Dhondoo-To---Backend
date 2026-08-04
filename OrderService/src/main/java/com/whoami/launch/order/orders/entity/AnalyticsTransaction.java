package com.whoami.launch.order.orders.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "analytics_transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shopId;

    private String orderId;

    private String customerId;

    private Long itemId;

    private String itemName;

    private String itemType; // PRODUCT,SERVICE

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalAmount;

    private LocalDateTime soldAt;
}