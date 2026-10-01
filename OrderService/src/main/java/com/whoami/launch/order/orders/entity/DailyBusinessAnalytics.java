package com.whoami.launch.order.orders.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "daily_business_analytics",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "businessId",
                                "analyticsDate"
                        }
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyBusinessAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            unique = true,
            nullable = false,
            updatable = false,
            length = 50
    )
    private String analyticsId;

    @Column(nullable = false, length = 40)
    private String businessId;

    @Column(nullable = false)
    private LocalDate analyticsDate;

    @Builder.Default
    private Integer totalOrders = 0;

    @Builder.Default
    private Integer totalProductsSold = 0;

    @Builder.Default
    private Integer totalServicesBooked = 0;

    @Builder.Default
    private BigDecimal totalRevenue = BigDecimal.ZERO;

    @PrePersist
    public void prePersist() {

        if (analyticsId == null) {
            analyticsId = "ANALYTICS_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }
    }
}