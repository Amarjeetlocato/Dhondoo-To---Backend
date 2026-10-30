package com.whoami.launch.order.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryEstimateRequest {
    private String shopId;
    private String businessId;
    private String deliveryAddress;
    private Double customerLat;
    private Double customerLng;
}
