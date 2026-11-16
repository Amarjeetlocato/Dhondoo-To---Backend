package com.whoami.billing.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.whoami.billing.domain.entity.BillingTransactionStatus;
import com.whoami.billing.domain.entity.Payment;
import com.whoami.billing.domain.entity.PaymentGateway;
import com.whoami.billing.domain.entity.PaymentStatus;
import com.whoami.billing.dto.request.CreateBillingRequest;
import com.whoami.billing.dto.request.CreatePaymentRequest;
import com.whoami.billing.dto.response.BillingTransactionResponse;
import com.whoami.billing.dto.response.PaymentResponse;
import com.whoami.billing.gateway.PaymentGatewayClient;
import com.whoami.billing.gateway.PaymentGatewayFactory;
import com.whoami.billing.kafka.BillingEventProducer;
import com.whoami.billing.repository.PaymentRepository;
import com.whoami.billing.service.BillingTransactionService;
import com.whoami.billing.service.PaymentService;
import com.whoami.billing.service.SubscriptionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final BillingEventProducer billingEventProducer;
    private final BillingTransactionService billingTransactionService;
    private final SubscriptionService subscriptionService;
    private final PaymentGatewayFactory paymentGatewayFactory;

    @Override
    @Transactional
    public PaymentResponse create(CreatePaymentRequest request) {

        BillingTransactionResponse transaction;
        try {
            transaction = billingTransactionService.getById(request.getTransactionId());
        } catch (Exception e) {
            log.info("BillingTransaction not found for ID: {}. Auto-creating pending transaction.", request.getTransactionId());
            transaction = billingTransactionService.create(CreateBillingRequest.builder()
                    .customerId("USER_" + request.getTransactionId().toString().substring(0, 8))
                    .referenceType("PAYMENT")
                    .referenceId(request.getTransactionId().toString())
                    .purpose("ORDER_CHECKOUT")
                    .amount(request.getAmount())
                    .currency(request.getCurrency())
                    .idempotencyKey(request.getTransactionId().toString())
                    .build());
        }

        String finalCurrency = (request.getCurrency() != null && !request.getCurrency().isBlank())
                ? request.getCurrency()
                : transaction.getCurrency();

        BigDecimal finalAmount = request.getAmount() != null
                ? request.getAmount()
                : transaction.getAmount();

        if (!transaction.getCurrency().equalsIgnoreCase(finalCurrency)) {
            throw new IllegalArgumentException(
                    "Payment currency does not match billing transaction currency"
            );
        }

        if (transaction.getAmount().compareTo(finalAmount) != 0) {
            throw new IllegalArgumentException(
                    "Payment amount (" + finalAmount + ") does not match server-enforced billing transaction amount (" + transaction.getAmount() + ")"
            );
        }

        BillingTransactionStatus transactionStatus =
                transaction.getStatus();

        if (transactionStatus == BillingTransactionStatus.PAID
                || transactionStatus == BillingTransactionStatus.REFUNDED
                || transactionStatus == BillingTransactionStatus.PARTIALLY_REFUNDED) {
            throw new IllegalStateException(
                    "Payment cannot be created for transaction with status: "
                            + transactionStatus
            );
        }

        Payment existingPayment =
                repository.findFirstByTransactionId(
                        request.getTransactionId()
                ).orElse(null);

        if (existingPayment != null) {
            return mapToResponse(existingPayment);
        }

        PaymentGatewayClient gatewayClient =
                paymentGatewayFactory.getClient(request.getGateway());

        String gatewayOrderId =
                gatewayClient.createPaymentOrder(
                        transaction.getTransactionId(),
                        finalAmount,
                        finalCurrency
                );

        Payment payment = Payment.builder()
                .transactionId(request.getTransactionId())
                .gateway(request.getGateway())
                .gatewayOrderId(gatewayOrderId)
                .amount(finalAmount)
                .currency(finalCurrency)
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.CREATED)
                .build();

        Payment saved = repository.save(payment);

        log.info(
                "Payment created: paymentId={}, transactionId={}, gateway={}, gatewayOrderId={}",
                saved.getId(),
                saved.getTransactionId(),
                saved.getGateway(),
                saved.getGatewayOrderId()
        );

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public PaymentResponse markPaymentSuccess(
            UUID paymentId,
            String gatewayPaymentId,
            String gatewaySignature) {

        Payment payment = repository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setGatewayPaymentId(gatewayPaymentId);
        payment.setGatewaySignature(gatewaySignature);

        Payment saved = repository.save(payment);
        billingTransactionService.markAsPaid(saved.getTransactionId());

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public PaymentResponse markPaymentFailed(UUID paymentId, String reason) {

        Payment payment = repository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason);

        Payment saved = repository.save(payment);
        billingTransactionService.markAsFailed(saved.getTransactionId());

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public PaymentResponse markAsRefunded(UUID paymentId) {
        Payment payment = repository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        payment.setStatus(PaymentStatus.REFUNDED);
        Payment saved = repository.save(payment);
        billingTransactionService.markAsRefunded(saved.getTransactionId());

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public PaymentResponse markAsPartiallyRefunded(UUID paymentId) {
        Payment payment = repository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
        Payment saved = repository.save(payment);
        billingTransactionService.markAsPartiallyRefunded(saved.getTransactionId());

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public PaymentResponse retryPayment(UUID paymentId) {
        Payment payment = repository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        payment.setStatus(PaymentStatus.PENDING);
        Payment saved = repository.save(payment);

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public PaymentResponse refundPayment(UUID paymentId, String reason) {
        Payment payment = repository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Only successful payments can be refunded. Current status: " + payment.getStatus());
        }

        BillingTransactionResponse transaction = billingTransactionService.getById(payment.getTransactionId());
        String purpose = transaction.getPurpose() != null ? transaction.getPurpose().toUpperCase().trim() : "";
        String refType = transaction.getReferenceType() != null ? transaction.getReferenceType().toUpperCase().trim() : "";

        BigDecimal refundableAmount;
        boolean isPartial;

        if (purpose.contains("COMBO") || refType.contains("COMBO")) {
            // Special Combo Offer Refund Policy:
            // Total Paid: ₹399.00
            // Non-Refundable Verification Processing Fee: ₹149.00 (deducted/retained)
            // Amount Returned to Customer: Exactly ₹250.00
            refundableAmount = new BigDecimal("250.00");
            isPartial = true;
        } else if (purpose.contains("VERIFICATION") || refType.contains("VERIFICATION")) {
            // Standalone Verification Fee: Strictly Non-Refundable
            throw new IllegalStateException("Business verification processing fee of ₹149.00 is strictly non-refundable.");
        } else {
            // Standalone Subscription or other payments: Full refund
            refundableAmount = payment.getAmount();
            isPartial = false;
        }

        // Trigger Payment Gateway refund (Razorpay) if live gateway payment ID exists
        if (payment.getGatewayPaymentId() != null && !payment.getGatewayPaymentId().isBlank()
                && payment.getGateway() == PaymentGateway.RAZORPAY) {
            try {
                PaymentGatewayClient gatewayClient = paymentGatewayFactory.getClient(payment.getGateway());
                gatewayClient.refundPayment(payment.getGatewayPaymentId(), refundableAmount);
                log.info("Gateway refund executed successfully for paymentId={}, amount={}", paymentId, refundableAmount);
            } catch (Exception e) {
                log.warn("Gateway refund warning: {}. Proceeding with internal ledger update.", e.getMessage());
            }
        }

        if (isPartial) {
            payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
            payment.setFailureReason("Partially refunded ₹" + refundableAmount + " (Non-refundable verification fee ₹149 retained). Reason: " + (reason != null ? reason : "Customer request"));
            Payment saved = repository.save(payment);
            billingTransactionService.markAsPartiallyRefunded(saved.getTransactionId());
            log.info("Combo payment partially refunded: paymentId={}, returned={}, retained=149.00", paymentId, refundableAmount);
            return mapToResponse(saved);
        } else {
            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setFailureReason("Refunded ₹" + refundableAmount + ". Reason: " + (reason != null ? reason : "Customer request"));
            Payment saved = repository.save(payment);
            billingTransactionService.markAsRefunded(saved.getTransactionId());
            log.info("Payment fully refunded: paymentId={}, returned={}", paymentId, refundableAmount);
            return mapToResponse(saved);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getById(UUID id) {

        return repository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Payment not found: " + id
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getByGatewayOrderId(String gatewayOrderId) {

        return repository.findByGatewayOrderId(gatewayOrderId)
                .map(this::mapToResponse)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Payment not found for gatewayOrderId: "
                                        + gatewayOrderId
                        )
                );
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getByGatewayPaymentId(String gatewayPaymentId) {

        return repository.findByGatewayPaymentId(gatewayPaymentId)
                .map(this::mapToResponse)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Payment not found for gatewayPaymentId: "
                                        + gatewayPaymentId
                        )
                );
    }

    private PaymentResponse mapToResponse(Payment payment) {

        return PaymentResponse.builder()
                .id(payment.getId())
                .transactionId(payment.getTransactionId())
                .gateway(payment.getGateway())
                .gatewayOrderId(payment.getGatewayOrderId())
                .gatewayPaymentId(payment.getGatewayPaymentId())
                .gatewaySignature(payment.getGatewaySignature())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .failureReason(payment.getFailureReason())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
