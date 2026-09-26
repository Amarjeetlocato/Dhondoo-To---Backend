package com.whoami.billing.gateway;

import com.whoami.billing.domain.entity.PaymentGateway;

import java.math.BigDecimal;

public interface PaymentGatewayClient {

    PaymentGateway getGateway();

    String createPaymentOrder(
            String transactionId,
            BigDecimal amount,
            String currency
    );

    String createRecurringPayment(
            String transactionId,
            BigDecimal amount,
            String currency,
            String gatewaySubscriptionId
    );

    boolean verifyPayment(
            String gatewayOrderId,
            String gatewayPaymentId
    );

    void refundPayment(
            String gatewayPaymentId,
            BigDecimal amount
    );
}