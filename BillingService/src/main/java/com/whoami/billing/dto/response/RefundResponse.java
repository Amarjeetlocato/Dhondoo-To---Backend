package com.whoami.billing.dto.response;

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
public class RefundResponse {

    private UUID id;

    private UUID paymentId;

    private String gatewayRefundId;

    private BigDecimal amount;

    private String currency;

    private String reason;

    private PaymentStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}