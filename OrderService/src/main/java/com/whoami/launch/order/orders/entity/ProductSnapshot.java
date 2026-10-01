package com.whoami.launch.order.orders.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "product_snapshots")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            unique = true,
            nullable = false,
            updatable = false,
            length = 55
    )
    private String snapshotId;

    @Column(nullable = false, length = 40)
    private String productId;

    @Column(nullable = false, length = 40)
    private String businessId;

    @Column(length = 1024)
    private String businessName;

    @Column(length = 2048)
    private String businessLogo;

    @Column(length = 40)
    private String userId;

    @Column(length = 500)
    private String productName;

    private BigDecimal productPrice;

    @Column(length = 2048)
    private String productImage;

    @PrePersist
    public void prePersist() {

        if (snapshotId == null) {
            snapshotId = "PRODUCT_SNAPSHOT_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }
    }
}