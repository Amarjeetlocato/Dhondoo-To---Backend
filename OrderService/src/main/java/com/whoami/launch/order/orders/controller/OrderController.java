package com.whoami.launch.order.orders.controller;

import com.whoami.launch.order.orders.dto.OrderResponse;
import com.whoami.launch.order.orders.dto.OrderStatusUpdateRequest;
import com.whoami.launch.order.orders.dto.PlaceOrderRequest;
import com.whoami.launch.order.orders.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    public ResponseEntity<List<OrderResponse>> placeOrder(@Valid @RequestBody PlaceOrderRequest request,
                                                          @RequestParam String customerId) {
        return ResponseEntity.ok(orderService.placeOrder(customerId, request));
    }

    @GetMapping("/customer")
    public ResponseEntity<List<OrderResponse>> getCustomerOrders(@RequestParam String customerId) {
        return ResponseEntity.ok(orderService.getCustomerOrders(customerId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable String orderId,
                                                         @RequestParam String customerId,
                                                         @RequestParam(required = false) String shopId) {
        return ResponseEntity.ok(orderService.getOrderDetails(orderId, customerId, shopId));
    }

    @GetMapping("/shop/orders")
    public ResponseEntity<List<OrderResponse>> getShopOrders(@RequestParam String shopId) {
        return ResponseEntity.ok(orderService.getShopOrders(shopId));
    }

    @PutMapping("/shop/orders/status/{orderId}")
    public ResponseEntity<OrderResponse> updateOrderStatus(@PathVariable String orderId,
                                                           @RequestParam String shopId,
                                                           @Valid @RequestBody OrderStatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, request.getStatus(), shopId));
    }
}
