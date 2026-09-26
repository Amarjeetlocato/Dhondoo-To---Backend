package com.locato.dto.billing.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundCompletedEvent {

    private UUID eventId;

    private String eventType;

    private LocalDateTime timestamp;

    private String source;

    private String transactionId;

    private String paymentId;

    private String refundId;

    private String customerId;

    private String referenceType;

    private String referenceId;

    private BigDecimal amount;

    private String currency;

    private String reason;
}