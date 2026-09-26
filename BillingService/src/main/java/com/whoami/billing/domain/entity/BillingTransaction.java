package com.whoami.billing.domain.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "billing_transaction",
        indexes = {
                @Index(name = "idx_billing_customer", columnList = "customer_id"),
                @Index(name = "idx_billing_reference", columnList = "reference_type, reference_id"),
                @Index(name = "idx_billing_idempotency", columnList = "idempotency_key", unique = true)
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "transaction_id", nullable = false, unique = true, length = 100)
    private String transactionId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "reference_type", nullable = false, length = 50)
    private String referenceType;

    @Column(name = "reference_id", nullable = false, length = 100)
    private String referenceId;

    @Column(nullable = false, length = 50)
    private String purpose;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private BillingTransactionStatus status;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 150)
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        if (transactionId == null || transactionId.isBlank()) {
            transactionId = "TXN-" + UUID.randomUUID();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}