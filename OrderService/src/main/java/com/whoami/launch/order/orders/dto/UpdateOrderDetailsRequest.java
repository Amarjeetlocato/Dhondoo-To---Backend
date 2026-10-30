package com.whoami.launch.order.orders.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOrderDetailsRequest {
    private String customerName;
    private String customerPhone;
    private String deliveryAddress;
    private BigDecimal subtotal;
    private BigDecimal deliveryFee;
    private List<OrderItemUpdateDto> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemUpdateDto {
        private String productId;
        private String productNameSnapshot;
        private String imageSnapshot;
        private BigDecimal priceSnapshot;
        private Integer quantity;
        private BigDecimal totalPrice;
    }
}
