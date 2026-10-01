package com.whoami.launch.order.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private String cartId;

    private String customerId;

    private String businessId;

    private String productId;

    private String productNameSnapshot;

    private String imageSnapshot;

    private String businessNameSnapshot;

    private String businessLogoSnapshot;

    private BigDecimal priceSnapshot;

    private Integer quantity;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}