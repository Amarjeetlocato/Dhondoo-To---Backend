package com.whoami.launch.order.delivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingResult {

    private String trackingId;
    private String deliveryStatus;
    private Double currentLat;
    private Double currentLng;
    private String deliveryBoyName;
    private String deliveryBoyPhone;
    private String estimatedDeliveryTime;
}
