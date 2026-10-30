package com.whoami.launch.order.delivery;

import com.whoami.launch.order.orders.dto.DispatchParcelRequest;
import com.whoami.launch.order.orders.entity.Order;

public interface DeliveryGatewayClient {

    String getPartnerName();

    DispatchResult dispatchParcel(Order order, DispatchParcelRequest request);

    TrackingResult trackParcel(String trackingId);
}
