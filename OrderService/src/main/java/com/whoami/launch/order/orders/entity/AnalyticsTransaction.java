package com.whoami.launch.order.orders.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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

    @Column(
            unique = true,
            nullable = false,
            updatable = false,
            length = 65
    )
    private String transactionId;

    @Column(nullable = false, length = 40)
    private String businessId;

    @Column(nullable = false, length = 40)
    private String orderId;

    @Column(nullable = false, length = 40)
    private String customerId;

    /**
     * Can be PRODUCT_... or SERVICE_...
     */
    @Column(nullable = false, length = 55)
    private String itemId;

    @Column(length = 500)
    private String itemName;

    @Column(length = 30)
    private String itemType;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalAmount;

    private LocalDateTime soldAt;

    @PrePersist
    public void prePersist() {

        if (transactionId == null) {
            transactionId = "ANALYTICS_TRANSACTION_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }
    }
}