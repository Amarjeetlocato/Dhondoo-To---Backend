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

    private String businessId;

    private OrderStatus status;

    private BigDecimal subtotal;

    private String customerNote;

    private String deliveryAddress;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<OrderItemResponse> items;
}