package com.whoami.launch.order.delivery;

import com.whoami.launch.order.orders.dto.DispatchParcelRequest;
import com.whoami.launch.order.orders.entity.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component("shiprocketDeliveryGatewayClient")
public class ShiprocketDeliveryGatewayClient implements DeliveryGatewayClient {

    @Override
    public String getPartnerName() {
        return "SHIPROCKET";
    }

    @Override
    public DispatchResult dispatchParcel(Order order, DispatchParcelRequest request) {
        log.info("Dispatching parcel via Shiprocket Courier API for orderId={}", order.getOrderId());

        String waybill = "SR_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return DispatchResult.builder()
                .trackingId(waybill)
                .deliveryPartner("SHIPROCKET")
                .deliveryBoyName("Shiprocket Express Courier")
                .deliveryBoyPhone("+91-1800102030")
                .startLat(request.getStartLat() != null ? request.getStartLat() : 28.6139)
                .startLng(request.getStartLng() != null ? request.getStartLng() : 77.2090)
                .estimatedDeliveryTime("Same-day / Express Delivery")
                .trackingUrl("https://shiprocket.co/tracking/" + waybill)
                .status("DISPATCHED")
                .build();
    }

    @Override
    public TrackingResult trackParcel(String trackingId) {
        return TrackingResult.builder()
                .trackingId(trackingId)
                .deliveryStatus("OUT_FOR_DELIVERY")
                .currentLat(28.6145)
                .currentLng(77.2092)
                .deliveryBoyName("Shiprocket Express Agent")
                .deliveryBoyPhone("+91-1800102030")
                .estimatedDeliveryTime("Out for delivery")
                .build();
    }
}
