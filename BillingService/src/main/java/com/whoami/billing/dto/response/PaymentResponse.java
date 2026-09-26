package com.whoami.billing.dto.response;

import com.whoami.billing.domain.entity.PaymentGateway;
import com.whoami.billing.domain.entity.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private UUID id;

    private UUID transactionId;

    private PaymentGateway gateway;

    private String gatewayOrderId;

    private String gatewayPaymentId;

    private BigDecimal amount;

    private String currency;

    private PaymentStatus status;

    private String paymentMethod;

    private LocalDateTime paidAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}