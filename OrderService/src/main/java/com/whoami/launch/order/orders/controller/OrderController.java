package com.whoami.launch.order.orders.controller;

import com.whoami.launch.order.orders.dto.OrderResponse;
import com.whoami.launch.order.orders.dto.OrderStatusUpdateRequest;
import com.whoami.launch.order.orders.dto.PlaceOrderRequest;
import com.whoami.launch.order.orders.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<List<OrderResponse>> placeOrder(
            @Valid @RequestBody PlaceOrderRequest request,
            @RequestParam String customerId) {

        return ResponseEntity.ok(
                orderService.placeOrder(
                        customerId,
                        request));
    }

    @GetMapping("/customer")
    public ResponseEntity<List<OrderResponse>> getCustomerOrders(
            @RequestParam String customerId) {

        return ResponseEntity.ok(
                orderService.getCustomerOrders(
                        customerId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetails(
            @PathVariable String orderId,
            @RequestParam String customerId,
            @RequestParam(required = false) String businessId) {

        return ResponseEntity.ok(
                orderService.getOrderDetails(
                        orderId,
                        customerId,
                        businessId));
    }

    @GetMapping("/business/orders")
    public ResponseEntity<List<OrderResponse>> getBusinessOrders(
            @RequestParam String businessId) {

        return ResponseEntity.ok(
                orderService.getBusinessOrders(
                        businessId));
    }

    @PutMapping("/business/orders/status/{orderId}")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam String businessId,
            @Valid @RequestBody OrderStatusUpdateRequest request) {

        return ResponseEntity.ok(
                orderService.updateOrderStatus(
                        orderId,
                        request.getStatus(),
                        businessId));
    }
}