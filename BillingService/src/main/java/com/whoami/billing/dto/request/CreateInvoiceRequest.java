package com.whoami.billing.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateInvoiceRequest(

        @NotNull
        UUID transactionId
) {
}