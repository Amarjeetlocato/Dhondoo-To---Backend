package com.whoami.launch.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefundRequestDTO {

    @NotBlank(message = "Payment ID is required")
    private String paymentId;

    private String orderId;

    private String userId;

    private String businessId;

    @NotNull(message = "Refund amount is required")
    private BigDecimal amount;

    private String currency;

    private String reason;

    private String refundMethod;
}
