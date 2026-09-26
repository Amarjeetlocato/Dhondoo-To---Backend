package com.whoami.billing.dto.response;

import com.whoami.billing.domain.entity.BillingCycle;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingPlanResponse {

    private UUID id;

    private String name;

    private String description;

    private BigDecimal amount;

    private String currency;

    private BillingCycle billingCycle;

    private String planType;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}