package com.whoami.launch.order.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchParcelRequest {

    private String deliveryPartner;
    private String deliveryWebsiteApiUrl;
    private String deliveryBoyName;
    private String deliveryBoyPhone;
    private Double startLat;
    private Double startLng;
    private String estimatedDeliveryTime;
}
