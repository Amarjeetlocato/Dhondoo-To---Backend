package com.whoami.billing.service;

import com.whoami.billing.dto.request.CreateBillingRequest;
import com.whoami.billing.dto.response.BillingTransactionResponse;

import java.util.UUID;

public interface BillingTransactionService {

    BillingTransactionResponse create(CreateBillingRequest request);

    BillingTransactionResponse getById(UUID id);

    BillingTransactionResponse getByTransactionId(String transactionId);

    BillingTransactionResponse getByIdempotencyKey(String idempotencyKey);

    void markAsPaid(UUID transactionId);

    void markAsFailed(UUID transactionId);

    void markAsRefunded(UUID transactionId);

    void markAsPartiallyRefunded(UUID transactionId);
}