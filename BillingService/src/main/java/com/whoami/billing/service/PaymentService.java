package com.whoami.billing.service;

import com.whoami.billing.dto.request.CreatePaymentRequest;
import com.whoami.billing.dto.response.PaymentResponse;

import java.util.UUID;

public interface PaymentService {

    PaymentResponse create(CreatePaymentRequest request);

    PaymentResponse getById(UUID id);

    PaymentResponse getByGatewayOrderId(String gatewayOrderId);

    PaymentResponse getByGatewayPaymentId(String gatewayPaymentId);

    PaymentResponse markPaymentSuccess(
            UUID paymentId,
            String gatewayPaymentId
    );

    PaymentResponse markPaymentFailed(
            UUID paymentId,
            String reason
    );

    PaymentResponse markAsRefunded(UUID paymentId);

    PaymentResponse markAsPartiallyRefunded(UUID paymentId);
    
    PaymentResponse retryPayment(UUID paymentId);
}