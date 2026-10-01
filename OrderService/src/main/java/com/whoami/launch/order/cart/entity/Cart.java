package com.whoami.launch.order.cart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            unique = true,
            nullable = false,
            updatable = false,
            length = 40
    )
    private String cartId;

    @Column(nullable = false, length = 40)
    private String customerId;

    @Column(nullable = false, length = 40)
    private String businessId;

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

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        if (cartId == null) {
            cartId = "CART_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }

        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}