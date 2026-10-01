package com.locato.constants.events.billing;

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
public class BillingRequestedEvent {

    private UUID eventId;
    private BillingEventType eventType;
    private int eventVersion;
    private String source;
    private LocalDateTime occurredAt;
    private String correlationId;

    private String referenceType;
    private String referenceId;
    private String customerId;
    private String purpose;
    private BigDecimal amount;
    private String currency;
    private String idempotencyKey;
}