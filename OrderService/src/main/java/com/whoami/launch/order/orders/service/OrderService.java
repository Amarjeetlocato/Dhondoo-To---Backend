package com.whoami.launch.order.orders.service;

import com.whoami.launch.order.orders.dto.OrderResponse;
import com.whoami.launch.order.orders.dto.PlaceOrderRequest;
import com.whoami.launch.order.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    List<OrderResponse> placeOrder(String customerId, PlaceOrderRequest request);

    List<OrderResponse> getCustomerOrders(String customerId);

    List<OrderResponse> getShopOrders(String shopId);

    OrderResponse getOrderDetails(String orderId, String currentCustomerId, String currentShopId);

    OrderResponse updateOrderStatus(String orderId, OrderStatus newStatus, String shopId);

}
