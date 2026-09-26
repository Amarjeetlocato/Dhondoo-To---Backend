package com.whoami.billing.dto.response;

import com.whoami.billing.domain.entity.BillingTransactionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingTransactionResponse {

    private UUID id;

    private String transactionId;

    private String customerId;

    private String referenceType;

    private String referenceId;

    private String purpose;

    private BigDecimal amount;

    private String currency;

    private BillingTransactionStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}