package com.whoami.billing.gateway;

import com.whoami.billing.domain.entity.PaymentGateway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "billing.payment-gateway", havingValue = "mock")
public class MockPaymentGatewayClient implements PaymentGatewayClient {

	@Override
	public PaymentGateway getGateway() {
		return PaymentGateway.RAZORPAY;
	}

	@Override
	public String createPaymentOrder(String transactionId, BigDecimal amount, String currency) {

		return "MOCK_ORDER_" + UUID.randomUUID();
	}

	@Override
	public String createRecurringPayment(String transactionId, BigDecimal amount, String currency,
			String gatewaySubscriptionId) {

		return "MOCK_RECURRING_" + UUID.randomUUID();
	}

	@Override
	public boolean verifyPayment(String gatewayOrderId, String gatewayPaymentId, String gatewaySignature) {

		return gatewayOrderId != null && !gatewayOrderId.isBlank() && gatewayPaymentId != null
				&& !gatewayPaymentId.isBlank() && gatewaySignature != null && !gatewaySignature.isBlank();
	}

	@Override
	public void refundPayment(String gatewayPaymentId, BigDecimal amount) {

		// Mock implementation: no real refund is processed.
	}

}
