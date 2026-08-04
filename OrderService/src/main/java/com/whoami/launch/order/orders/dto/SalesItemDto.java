package com.whoami.launch.order.orders.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesItemDto {

    private String itemName;

    private String itemType;

    private Integer quantity;

    private BigDecimal amount;

    private LocalDateTime soldAt;
}