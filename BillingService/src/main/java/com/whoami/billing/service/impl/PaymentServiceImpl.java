package com.whoami.billing.service.impl;

import com.locato.dto.billing.event.PaymentCompletedEvent;
import com.locato.dto.billing.event.PaymentFailedEvent;
import com.whoami.billing.domain.entity.BillingTransactionStatus;
import com.whoami.billing.domain.entity.Payment;
import com.whoami.billing.domain.entity.PaymentStatus;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;
    private final BillingEventProducer billingEventProducer;
    private final BillingTransactionService billingTransactionService;
    private final SubscriptionService subscriptionService;
    private final PaymentGatewayFactory paymentGatewayFactory;

    @Override
    @Transactional
    public PaymentResponse create(CreatePaymentRequest request) {

        BillingTransactionResponse transaction =
                billingTransactionService.getById(
                        request.getTransactionId()
                );

        if (!transaction.getCurrency()
                .equalsIgnoreCase(request.getCurrency())) {

            throw new IllegalArgumentException(
                    "Payment currency does not match billing transaction currency"
            );
        }

        if (transaction.getAmount()
                .compareTo(request.getAmount()) != 0) {

            throw new IllegalArgumentException(
                    "Payment amount does not match billing transaction amount"
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
                paymentGatewayFactory.getClient(
                        request.getGateway()
                );

        String gatewayOrderId =
                gatewayClient.createPaymentOrder(
                        transaction.getTransactionId(),
                        request.getAmount(),
                        request.getCurrency()
                );

        Payment payment = Payment.builder()
                .transactionId(request.getTransactionId())
                .gateway(request.getGateway())
                .gatewayOrderId(gatewayOrderId)
                .amount(request.getAmount())
                .currency(request.getCurrency())
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
            String gatewayPaymentId) {

        Payment payment = findPayment(paymentId);

        /*
         * Idempotent success.
         */
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return mapToResponse(payment);
        }

        if (payment.getStatus() != PaymentStatus.CREATED
                && payment.getStatus() != PaymentStatus.PENDING) {

            throw new IllegalStateException(
                    "Payment cannot be marked successful from status: "
                            + payment.getStatus()
            );
        }

        if (gatewayPaymentId == null
                || gatewayPaymentId.isBlank()) {

            throw new IllegalArgumentException(
                    "Gateway payment ID is required for successful payment"
            );
        }

        PaymentGatewayClient gatewayClient =
                paymentGatewayFactory.getClient(
                        payment.getGateway()
                );

        boolean verified =
                gatewayClient.verifyPayment(
                        payment.getGatewayOrderId(),
                        gatewayPaymentId
                );

        if (!verified) {
            throw new IllegalStateException(
                    "Payment verification failed for gateway: "
                            + payment.getGateway()
            );
        }

        payment.setGatewayPaymentId(gatewayPaymentId);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaidAt(LocalDateTime.now());

        Payment saved = repository.save(payment);

        BillingTransactionResponse transaction =
                billingTransactionService.getById(
                        saved.getTransactionId()
                );

        billingTransactionService.markAsPaid(
                saved.getTransactionId()
        );

        /*
         * Subscription renewal.
         */
        if ("SUBSCRIPTION".equalsIgnoreCase(
                transaction.getReferenceType())) {

            UUID subscriptionId =
                    parseSubscriptionId(transaction);

            subscriptionService.renew(subscriptionId);
        }

        PaymentCompletedEvent event =
                PaymentCompletedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType("PAYMENT_COMPLETED")
                        .timestamp(LocalDateTime.now())
                        .source("BillingService")
                        .transactionId(
                                saved.getTransactionId().toString()
                        )
                        .paymentId(
                                saved.getId().toString()
                        )
                        .amount(saved.getAmount())
                        .currency(saved.getCurrency())
                        .paymentMethod(saved.getPaymentMethod())
                        .build();

        billingEventProducer.publishPaymentCompleted(event);

        log.info(
                "Payment marked successful: paymentId={}, transactionId={}, gatewayPaymentId={}",
                saved.getId(),
                saved.getTransactionId(),
                saved.getGatewayPaymentId()
        );

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public PaymentResponse markPaymentFailed(
            UUID paymentId,
            String reason) {

        Payment payment = findPayment(paymentId);

        /*
         * Idempotent failure.
         */
        if (payment.getStatus() == PaymentStatus.FAILED) {
            return mapToResponse(payment);
        }

        if (payment.getStatus() == PaymentStatus.SUCCESS
                || payment.getStatus() == PaymentStatus.REFUNDED
                || payment.getStatus() == PaymentStatus.PARTIALLY_REFUNDED) {

            throw new IllegalStateException(
                    "Payment cannot be marked failed from status: "
                            + payment.getStatus()
            );
        }

        payment.setStatus(PaymentStatus.FAILED);

        Payment saved = repository.save(payment);

        BillingTransactionResponse transaction =
                billingTransactionService.getById(
                        saved.getTransactionId()
                );

        billingTransactionService.markAsFailed(
                saved.getTransactionId()
        );

        /*
         * Subscription payment failure.
         */
        if ("SUBSCRIPTION".equalsIgnoreCase(
                transaction.getReferenceType())) {

            UUID subscriptionId =
                    parseSubscriptionId(transaction);

            subscriptionService.markPaymentFailed(
                    subscriptionId
            );
        }

        PaymentFailedEvent event =
                PaymentFailedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType("PAYMENT_FAILED")
                        .timestamp(LocalDateTime.now())
                        .source("BillingService")
                        .transactionId(
                                saved.getTransactionId().toString()
                        )
                        .paymentId(
                                saved.getId().toString()
                        )
                        .amount(saved.getAmount())
                        .currency(saved.getCurrency())
                        .reason(reason)
                        .build();

        billingEventProducer.publishPaymentFailed(event);

        log.info(
                "Payment marked failed: paymentId={}, transactionId={}, reason={}",
                saved.getId(),
                saved.getTransactionId(),
                reason
        );

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public PaymentResponse markAsRefunded(
            UUID paymentId) {

        Payment payment = findPayment(paymentId);

        /*
         * Idempotent refund.
         */
        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            return mapToResponse(payment);
        }

        if (payment.getStatus() != PaymentStatus.SUCCESS
                && payment.getStatus() != PaymentStatus.PARTIALLY_REFUNDED) {

            throw new IllegalStateException(
                    "Payment cannot be marked refunded from status: "
                            + payment.getStatus()
            );
        }

        payment.setStatus(PaymentStatus.REFUNDED);

        Payment saved = repository.save(payment);

        /*
         * Keep billing transaction state synchronized.
         */
        billingTransactionService.markAsRefunded(
                saved.getTransactionId()
        );

        log.info(
                "Payment marked refunded: paymentId={}, transactionId={}",
                saved.getId(),
                saved.getTransactionId()
        );

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public PaymentResponse markAsPartiallyRefunded(
            UUID paymentId) {

        Payment payment = findPayment(paymentId);

        /*
         * Idempotent partial refund.
         */
        if (payment.getStatus() == PaymentStatus.PARTIALLY_REFUNDED) {
            return mapToResponse(payment);
        }

        if (payment.getStatus() != PaymentStatus.SUCCESS) {

            throw new IllegalStateException(
                    "Payment cannot be marked partially refunded from status: "
                            + payment.getStatus()
            );
        }

        payment.setStatus(
                PaymentStatus.PARTIALLY_REFUNDED
        );

        Payment saved = repository.save(payment);

        /*
         * Keep billing transaction state synchronized.
         */
        billingTransactionService.markAsPartiallyRefunded(
                saved.getTransactionId()
        );

        log.info(
                "Payment marked partially refunded: paymentId={}, transactionId={}",
                saved.getId(),
                saved.getTransactionId()
        );

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getById(UUID id) {

        return mapToResponse(
                repository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Payment not found: " + id
                                )
                        )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getByGatewayOrderId(
            String gatewayOrderId) {

        return mapToResponse(
                repository.findByGatewayOrderId(
                                gatewayOrderId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Payment not found: "
                                                + gatewayOrderId
                                )
                        )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getByGatewayPaymentId(
            String gatewayPaymentId) {

        return mapToResponse(
                repository.findByGatewayPaymentId(
                                gatewayPaymentId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Payment not found: "
                                                + gatewayPaymentId
                                )
                        )
        );
    }

    @Override
    @Transactional
    public PaymentResponse retryPayment(UUID paymentId) {

        Payment payment =
                repository.findById(paymentId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Payment not found: " + paymentId
                                )
                        );

        if (payment.getStatus() != PaymentStatus.FAILED) {

            throw new IllegalStateException(
                    "Only failed payment can be retried"
            );
        }

        PaymentGatewayClient gatewayClient =
                paymentGatewayFactory.getClient(
                        payment.getGateway()
                );

        /*
         * Every retry gets a fresh gateway order.
         */
        String newGatewayOrderId =
                gatewayClient.createPaymentOrder(
                        payment.getTransactionId().toString(),
                        payment.getAmount(),
                        payment.getCurrency()
                );

        payment.setGatewayOrderId(newGatewayOrderId);
        payment.setGatewayPaymentId(null);
        payment.setPaidAt(null);
        payment.setStatus(PaymentStatus.CREATED);

        Payment saved = repository.save(payment);

        log.info(
                "Payment retry created: paymentId={}, transactionId={}, newGatewayOrderId={}",
                saved.getId(),
                saved.getTransactionId(),
                saved.getGatewayOrderId()
        );

        return mapToResponse(saved);
    }

    private Payment findPayment(UUID paymentId) {

        return repository.findById(paymentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Payment not found: " + paymentId
                        )
                );
    }

    private UUID parseSubscriptionId(
            BillingTransactionResponse transaction) {

        if (transaction.getReferenceId() == null
                || transaction.getReferenceId().isBlank()) {

            throw new IllegalStateException(
                    "Subscription reference ID is missing for transaction: "
                            + transaction.getTransactionId()
            );
        }

        try {

            return UUID.fromString(
                    transaction.getReferenceId()
            );

        } catch (IllegalArgumentException e) {

            throw new IllegalStateException(
                    "Invalid subscription reference ID: "
                            + transaction.getReferenceId(),
                    e
            );
        }
    }

    private PaymentResponse mapToResponse(
            Payment entity) {

        return PaymentResponse.builder()
                .id(entity.getId())
                .transactionId(entity.getTransactionId())
                .gateway(entity.getGateway())
                .gatewayOrderId(entity.getGatewayOrderId())
                .gatewayPaymentId(entity.getGatewayPaymentId())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .status(entity.getStatus())
                .paymentMethod(entity.getPaymentMethod())
                .paidAt(entity.getPaidAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
