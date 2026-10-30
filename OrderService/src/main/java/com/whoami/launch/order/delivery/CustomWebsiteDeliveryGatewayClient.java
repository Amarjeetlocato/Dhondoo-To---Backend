package com.whoami.launch.order.delivery;

import com.whoami.launch.order.orders.dto.DispatchParcelRequest;
import com.whoami.launch.order.orders.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component("customWebsiteDeliveryGatewayClient")
public class CustomWebsiteDeliveryGatewayClient implements DeliveryGatewayClient {

    @Override
    public String getPartnerName() {
        return "CUSTOM_WEBSITE";
    }

    @Override
    public DispatchResult dispatchParcel(Order order, DispatchParcelRequest request) {
        log.info("Dispatching parcel via Custom Delivery Website API for orderId={}, apiUrl={}",
                order.getOrderId(), request.getDeliveryWebsiteApiUrl());

        String trackingId = "TRK_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String partner = request.getDeliveryPartner() != null && !request.getDeliveryPartner().isBlank()
                ? request.getDeliveryPartner() : "CUSTOM_DELIVERY_API";

        return DispatchResult.builder()
                .trackingId(trackingId)
                .deliveryPartner(partner)
                .deliveryBoyName(request.getDeliveryBoyName() != null && !request.getDeliveryBoyName().isBlank()
                        ? request.getDeliveryBoyName() : "Inbuilt Express Rider")
                .deliveryBoyPhone(request.getDeliveryBoyPhone() != null && !request.getDeliveryBoyPhone().isBlank()
                        ? request.getDeliveryBoyPhone() : "+91-9876543210")
                .startLat(request.getStartLat() != null ? request.getStartLat() : 28.6139)
                .startLng(request.getStartLng() != null ? request.getStartLng() : 77.2090)
                .estimatedDeliveryTime(request.getEstimatedDeliveryTime() != null && !request.getEstimatedDeliveryTime().isBlank()
                        ? request.getEstimatedDeliveryTime() : "25-35 mins")
                .trackingUrl(request.getDeliveryWebsiteApiUrl())
                .status("DISPATCHED")
                .build();
    }

    @Override
    public TrackingResult trackParcel(String trackingId) {
        return TrackingResult.builder()
                .trackingId(trackingId)
                .deliveryStatus("OUT_FOR_DELIVERY")
                .currentLat(28.6150)
                .currentLng(77.2095)
                .deliveryBoyName("Inbuilt Express Rider")
                .deliveryBoyPhone("+91-9876543210")
                .estimatedDeliveryTime("15 mins remaining")
                .build();
    }
}
