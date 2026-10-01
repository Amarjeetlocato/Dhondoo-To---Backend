package com.whoami.billing.dto.response;

import com.whoami.billing.domain.entity.PaymentGateway;
import com.whoami.billing.domain.entity.SubscriptionStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {

    private String id;

    private String customerId;

    private UUID planId;

    private PaymentGateway gateway;

    private String gatewaySubscriptionId;

    private SubscriptionStatus status;

    private LocalDate startDate;

    private LocalDate nextBillingDate;

    private LocalDate endDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}