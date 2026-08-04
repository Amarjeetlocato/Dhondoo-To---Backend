package com.whoami.launch.order.orders.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "daily_shop_analytics",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "shopId",
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
public class DailyShopAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String shopId;

    private LocalDate analyticsDate;

    @Builder.Default
    private Integer totalOrders = 0;

    @Builder.Default
    private Integer totalProductsSold = 0;

    @Builder.Default
    private Integer totalServicesBooked = 0;

    @Builder.Default
    private BigDecimal totalRevenue = BigDecimal.ZERO;
}