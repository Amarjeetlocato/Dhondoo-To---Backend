package com.whoami.launch.order.cart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private Long id;
    private String userId;
    private String shopId;
    private String productId;
    private String productNameSnapshot;
    private String imageSnapshot;
    private BigDecimal priceSnapshot;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

   
}
