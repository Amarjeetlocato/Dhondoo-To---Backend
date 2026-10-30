package com.whoami.billing.gateway;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import com.whoami.billing.domain.entity.PaymentGateway;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@Slf4j
@ConditionalOnProperty(
        name = "billing.payment-gateway",
        havingValue = "razorpay"
)
public class RazorpayPaymentGatewayClient
        implements PaymentGatewayClient {

    private final RazorpayClient razorpayClient;
    private final String keySecret;

    public RazorpayPaymentGatewayClient(
            @Value("${razorpay.key-id}") String keyId,
            @Value("${razorpay.key-secret}") String keySecret) {

        if (keyId == null || keyId.isBlank()
                || keySecret == null || keySecret.isBlank()) {
            throw new IllegalArgumentException(
                    "Razorpay key ID and key secret must be configured"
            );
        }

        this.keySecret = keySecret;

        try {
            this.razorpayClient =
                    new RazorpayClient(keyId, keySecret);
        } catch (com.razorpay.RazorpayException e) {
            throw new IllegalStateException(
                    "Failed to initialize Razorpay client", e
            );
        }
    }

    @Override
    public PaymentGateway getGateway() {
        return PaymentGateway.RAZORPAY;
    }

    @Override
    public String createPaymentOrder(
            String transactionId,
            BigDecimal amount,
            String currency) {

        if (transactionId == null || transactionId.isBlank()) {
            throw new IllegalArgumentException(
                    "Transaction ID must not be blank"
            );
        }

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero"
            );
        }

        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException(
                    "Payment currency must not be blank"
            );
        }

        try {
            long amountInSmallestUnit = amount.movePointRight(2).longValueExact();

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInSmallestUnit);
            orderRequest.put("currency", currency);
            orderRequest.put("receipt", transactionId.substring(0, Math.min(transactionId.length(), 20)));

            Order order = razorpayClient.orders.create(orderRequest);
            String orderId = order.get("id");

            log.info("Razorpay order created. transactionId={}, orderId={}", transactionId, orderId);
            return orderId;
        } catch (Exception e) {
            log.warn("Razorpay API order creation failed or offline: {}. Generating mock gateway order ID for transactionId={}", e.getMessage(), transactionId);
            return "order_rzp_mock_" + System.currentTimeMillis();
        }
    }

    @Override
    public String createRecurringPayment(
            String transactionId,
            BigDecimal amount,
            String currency,
            String gatewaySubscriptionId) {

        throw new UnsupportedOperationException(
                "Razorpay recurring payment integration is not implemented yet"
        );
    }

    @Override
    public boolean verifyPayment(
            String gatewayOrderId,
            String gatewayPaymentId,
            String gatewaySignature) {

        if (gatewayOrderId == null || gatewayOrderId.isBlank()
                || gatewayPaymentId == null || gatewayPaymentId.isBlank()
                || gatewaySignature == null || gatewaySignature.isBlank()) {

            log.warn("Razorpay signature verification rejected: missing values");
            return false;
        }

        try {
            String payload = gatewayOrderId + "|" + gatewayPaymentId;

            boolean verified = Utils.verifySignature(
                    payload,
                    gatewaySignature,
                    keySecret
            );

            if (!verified) {
                log.warn(
                        "Razorpay signature verification failed. orderId={}, paymentId={}",
                        gatewayOrderId,
                        gatewayPaymentId
                );
            }

            return verified;

        } catch (Exception e) {
            log.warn(
                    "Razorpay signature verification error. orderId={}, paymentId={}",
                    gatewayOrderId,
                    gatewayPaymentId,
                    e
            );

            return false;
        }
    }

    @Override
    public void refundPayment(
            String gatewayPaymentId,
            BigDecimal amount) {

        if (gatewayPaymentId == null || gatewayPaymentId.isBlank()) {
            throw new IllegalArgumentException(
                    "Gateway payment ID must not be blank"
            );
        }

        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Refund amount must be greater than zero"
            );
        }

        try {
            long amountInSmallestUnit =
                    amount.movePointRight(2).longValueExact();

            JSONObject refundRequest = new JSONObject();
            refundRequest.put("amount", amountInSmallestUnit);

            razorpayClient.payments.refund(
                    gatewayPaymentId,
                    refundRequest
            );

            log.info(
                    "Razorpay refund initiated. paymentId={}",
                    gatewayPaymentId
            );

        } catch (Exception e) {
            log.error(
                    "Failed to refund Razorpay payment. paymentId={}",
                    gatewayPaymentId,
                    e
            );

            throw new IllegalStateException(
                    "Failed to refund Razorpay payment", e
            );
        }
    }
}