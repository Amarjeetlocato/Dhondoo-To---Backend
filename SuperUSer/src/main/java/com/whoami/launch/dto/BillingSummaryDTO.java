package com.whoami.launch.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillingSummaryDTO {

    private BigDecimal totalRevenue;
    private BigDecimal totalRefunds;
    private Long successfulTransactions;
    private Long failedTransactions;
    private Long activeSubscriptions;
    private Long totalOrders;
}
