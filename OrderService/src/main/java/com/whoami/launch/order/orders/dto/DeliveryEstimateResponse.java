package com.whoami.launch.order.orders.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryEstimateResponse {
    private BigDecimal deliveryFee;
    private String estimatedTime;
    private String deliveryPartner;
    private Double distanceKm;
}
