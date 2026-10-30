package com.whoami.launch.order.delivery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchResult {

    private String trackingId;
    private String deliveryPartner;
    private String deliveryBoyName;
    private String deliveryBoyPhone;
    private Double startLat;
    private Double startLng;
    private String estimatedDeliveryTime;
    private String trackingUrl;
    private String status;
}
