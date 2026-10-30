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

    @PostMapping("/{orderId}/dispatch-parcel")
    public ResponseEntity<OrderResponse> dispatchParcel(
            @PathVariable String orderId,
            @RequestParam String businessId,
            @RequestBody com.whoami.launch.order.orders.dto.DispatchParcelRequest request) {

        return ResponseEntity.ok(
                orderService.dispatchParcel(
                        orderId,
                        businessId,
                        request));
    }

    @GetMapping("/{orderId}/tracking")
    public ResponseEntity<OrderResponse> getTracking(
            @PathVariable String orderId) {

        return ResponseEntity.ok(
                orderService.getTracking(orderId));
    }

    @PutMapping("/{orderId}/delivery-location")
    public ResponseEntity<OrderResponse> updateDeliveryLocation(
            @PathVariable String orderId,
            @RequestBody com.whoami.launch.order.orders.dto.UpdateDeliveryLocationRequest request) {

        return ResponseEntity.ok(
                orderService.updateDeliveryLocation(
                        orderId,
                        request));
    }

    @PostMapping("/estimate-delivery-fee")
    public ResponseEntity<com.whoami.launch.order.orders.dto.DeliveryEstimateResponse> estimateDeliveryFee(
            @RequestBody com.whoami.launch.order.orders.dto.DeliveryEstimateRequest request) {

        return ResponseEntity.ok(
                orderService.estimateDeliveryFee(request));
    }

    @PutMapping("/{orderId}/update-order-details")
    public ResponseEntity<OrderResponse> updateOrderDetails(
            @PathVariable String orderId,
            @RequestParam String businessId,
            @RequestBody com.whoami.launch.order.orders.dto.UpdateOrderDetailsRequest request) {

        return ResponseEntity.ok(
                orderService.updateOrderDetails(orderId, businessId, request));
    }
}