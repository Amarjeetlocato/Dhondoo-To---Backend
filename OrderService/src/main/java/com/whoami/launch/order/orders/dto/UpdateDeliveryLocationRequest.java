package com.whoami.launch.order.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDeliveryLocationRequest {

    private Double latitude;
    private Double longitude;
    private String deliveryStatus;
}
