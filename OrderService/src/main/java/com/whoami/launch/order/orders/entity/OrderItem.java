package com.whoami.launch.order.orders.entity;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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

    @Column(
            unique = true,
            nullable = false,
            updatable = false,
            length = 45
    )
    private String orderItemId;

    @Column(nullable = false, length = 40)
    private String orderId;

    @Column(nullable = false, length = 40)
    private String productId;

    @Column(length = 1024)
    private String productNameSnapshot;

    @Column(length = 2048)
    private String imageSnapshot;

    @Column(length = 1024)
    private String businessNameSnapshot;

    @Column(length = 2048)
    private String businessLogoSnapshot;

    private BigDecimal priceSnapshot;

    private Integer quantity;

    private BigDecimal totalPrice;

    @PrePersist
    public void prePersist() {

        if (orderItemId == null) {
            orderItemId = "ORDER_ITEM_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }
    }
}