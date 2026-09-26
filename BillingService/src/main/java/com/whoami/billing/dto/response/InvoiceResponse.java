package com.whoami.billing.dto.response;

import com.whoami.billing.domain.entity.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record InvoiceResponse(

        UUID id,
        String invoiceNumber,
        UUID transactionId,
        String customerId,
        String referenceType,
        String referenceId,
        String purpose,
        BigDecimal amount,
        String currency,
        InvoiceStatus status,
        LocalDateTime issuedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}