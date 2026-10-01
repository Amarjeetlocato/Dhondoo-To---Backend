package com.whoami.launch.order.orders.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private String productId;

    private String productNameSnapshot;

    private String imageSnapshot;

    private BigDecimal priceSnapshot;

    private Integer quantity;

    private BigDecimal totalPrice;
}