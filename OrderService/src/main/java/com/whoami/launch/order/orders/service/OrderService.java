package com.whoami.launch.order.orders.service;

import com.whoami.launch.order.enums.OrderStatus;
import com.whoami.launch.order.orders.dto.OrderResponse;
import com.whoami.launch.order.orders.dto.PlaceOrderRequest;

import java.util.List;

public interface OrderService {

    List<OrderResponse> placeOrder(
            String customerId,
            PlaceOrderRequest request
    );

    List<OrderResponse> getCustomerOrders(
            String customerId
    );

    List<OrderResponse> getBusinessOrders(
            String businessId
    );

    OrderResponse getOrderDetails(
            String orderId,
            String currentCustomerId,
            String currentBusinessId
    );

    OrderResponse updateOrderStatus(
            String orderId,
            OrderStatus newStatus,
            String businessId
    );

    OrderResponse dispatchParcel(
            String orderId,
            String businessId,
            com.whoami.launch.order.orders.dto.DispatchParcelRequest request
    );

    OrderResponse getTracking(String orderId);

    OrderResponse updateDeliveryLocation(
            String orderId,
            com.whoami.launch.order.orders.dto.UpdateDeliveryLocationRequest request
    );

    com.whoami.launch.order.orders.dto.DeliveryEstimateResponse estimateDeliveryFee(
            com.whoami.launch.order.orders.dto.DeliveryEstimateRequest request
    );

    OrderResponse updateOrderDetails(
            String orderId,
            String businessId,
            com.whoami.launch.order.orders.dto.UpdateOrderDetailsRequest request
    );
}