package com.whoami.launch.order.orders.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsSummaryDto {

    private Integer totalOrders;

    private Integer totalProductsSold;

    private Integer totalServicesBooked;

    private BigDecimal totalRevenue;
}