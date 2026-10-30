package com.whoami.launch.order.orders.dto;

import com.whoami.launch.order.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private String orderId;

    private String customerId;

    private String customerPhone;

    private String businessId;

    private String businessName;

    private String businessLogo;

    private String businessBanner;

    private String businessPhone;

    private String businessAddress;

    private Double businessLat;

    private Double businessLng;

    private Double customerLat;

    private Double customerLng;

    private String fulfillmentType;

    private BigDecimal deliveryFee;

    private OrderStatus status;

    private BigDecimal subtotal;

    private String customerNote;

    private String deliveryAddress;

    private String deliveryPartner;

    private String trackingId;

    private String deliveryStatus;

    private String deliveryBoyName;

    private String deliveryBoyPhone;

    private Double deliveryBoyLat;

    private Double deliveryBoyLng;

    private String estimatedDeliveryTime;

    private String dispatchApiUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<OrderItemResponse> items;
}