package com.whoami.billing.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.locato.dto.billing.event.RefundCompletedEvent;
import com.whoami.billing.domain.entity.PaymentStatus;
import com.whoami.billing.domain.entity.Refund;
import com.whoami.billing.dto.request.RefundRequest;
import com.whoami.billing.dto.response.BillingTransactionResponse;
import com.whoami.billing.dto.response.PaymentResponse;
import com.whoami.billing.dto.response.RefundResponse;
import com.whoami.billing.kafka.BillingEventProducer;
import com.whoami.billing.repository.RefundRepository;
import com.whoami.billing.service.BillingTransactionService;
import com.whoami.billing.service.InvoiceService;
import com.whoami.billing.service.PaymentService;
import com.whoami.billing.service.RefundService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RefundServiceImpl implements RefundService {

    private final RefundRepository repository;
    private final BillingEventProducer billingEventProducer;
    private final PaymentService paymentService;
    private final BillingTransactionService billingTransactionService;
    private final InvoiceService invoiceService;

    @Override
    public RefundResponse create(RefundRequest request) {

        PaymentResponse payment = paymentService.getById(
                request.getPaymentId()
        );

        /*
         * Refund can only be created for a successful payment
         * or a payment that has already been partially refunded.
         */
        if (payment.getStatus() != PaymentStatus.SUCCESS
                && payment.getStatus() != PaymentStatus.PARTIALLY_REFUNDED) {

            throw new IllegalStateException(
                    "Refund cannot be created for payment with status: "
                            + payment.getStatus()
            );
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Refund amount must be greater than zero"
            );
        }

        if (request.getAmount().compareTo(payment.getAmount()) > 0) {
            throw new IllegalArgumentException(
                    "Refund amount cannot be greater than payment amount"
            );
        }

        BigDecimal refundedAmount =
                repository.sumRefundAmountByPaymentIdAndStatus(
                        request.getPaymentId(),
                        PaymentStatus.SUCCESS
                );

        BigDecimal totalRefundAmount =
                refundedAmount.add(request.getAmount());

        if (totalRefundAmount.compareTo(payment.getAmount()) > 0) {
            throw new IllegalArgumentException(
                    "Total refund amount cannot be greater than payment amount"
            );
        }

        Refund refund = Refund.builder()
                .paymentId(request.getPaymentId())
                .amount(request.getAmount())
                .currency(payment.getCurrency())
                .reason(request.getReason())
                .status(PaymentStatus.PENDING)
                .build();

        Refund saved = repository.save(refund);

        return mapToResponse(saved);
    }

    @Override
    public RefundResponse markRefundCompleted(
            UUID refundId,
            String gatewayRefundId) {

        Refund refund = repository.findById(refundId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Refund not found: " + refundId
                        )
                );

        /*
         * Idempotency:
         * If the gateway sends the same completion callback again,
         * do not process the refund again.
         */
        if (refund.getStatus() == PaymentStatus.SUCCESS) {
            return mapToResponse(refund);
        }

        /*
         * Only PENDING refunds can be completed.
         */
        if (refund.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException(
                    "Refund cannot be completed from status: "
                            + refund.getStatus()
            );
        }

        if (gatewayRefundId == null
                || gatewayRefundId.isBlank()) {

            throw new IllegalArgumentException(
                    "Gateway refund ID is required"
            );
        }

        PaymentResponse payment = paymentService.getById(
                refund.getPaymentId()
        );

        /*
         * Payment must be in a refundable state.
         */
        if (payment.getStatus() != PaymentStatus.SUCCESS
                && payment.getStatus() != PaymentStatus.PARTIALLY_REFUNDED) {

            throw new IllegalStateException(
                    "Refund cannot be completed for payment with status: "
                            + payment.getStatus()
            );
        }

        BigDecimal refundedAmount =
                repository.sumRefundAmountByPaymentIdAndStatus(
                        refund.getPaymentId(),
                        PaymentStatus.SUCCESS
                );

        BigDecimal totalRefundAmount =
                refundedAmount.add(refund.getAmount());

        if (totalRefundAmount.compareTo(payment.getAmount()) > 0) {
            throw new IllegalStateException(
                    "Total refund amount cannot be greater than payment amount"
            );
        }

        refund.setGatewayRefundId(gatewayRefundId);
        refund.setStatus(PaymentStatus.SUCCESS);

        Refund saved = repository.save(refund);

        boolean fullRefund =
                totalRefundAmount.compareTo(payment.getAmount()) == 0;

        if (fullRefund) {

            paymentService.markAsRefunded(
                    payment.getId()
            );

            billingTransactionService.markAsRefunded(
                    payment.getTransactionId()
            );

            updateInvoiceAfterRefund(
                    payment.getTransactionId(),
                    true
            );

        } else {

            paymentService.markAsPartiallyRefunded(
                    payment.getId()
            );

            billingTransactionService.markAsPartiallyRefunded(
                    payment.getTransactionId()
            );

            updateInvoiceAfterRefund(
                    payment.getTransactionId(),
                    false
            );
        }

        BillingTransactionResponse transaction =
                billingTransactionService.getById(
                        payment.getTransactionId()
                );

        RefundCompletedEvent event =
                RefundCompletedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType("REFUND_COMPLETED")
                        .timestamp(LocalDateTime.now())
                        .source("BillingService")
                        .transactionId(
                                payment.getTransactionId().toString()
                        )
                        .paymentId(
                                saved.getPaymentId().toString()
                        )
                        .refundId(
                                saved.getId().toString()
                        )
                        .customerId(
                                transaction.getCustomerId()
                        )
                        .referenceType(
                                transaction.getReferenceType()
                        )
                        .referenceId(
                                transaction.getReferenceId()
                        )
                        .amount(saved.getAmount())
                        .currency(saved.getCurrency())
                        .reason(saved.getReason())
                        .build();

        billingEventProducer.publishRefundCompleted(event);

        return mapToResponse(saved);
    }

    /**
     * Invoice is optional for a refund.
     *
     * We check existence before calling the invoice service.
     * Therefore, a missing invoice does not throw an exception
     * and cannot mark the refund transaction as rollback-only.
     */
    private void updateInvoiceAfterRefund(
            UUID transactionId,
            boolean fullRefund) {

        if (!invoiceService.existsByTransactionId(transactionId)) {
            return;
        }

        if (fullRefund) {
            invoiceService.markAsRefundedForTransaction(
                    transactionId
            );
        } else {
            invoiceService.markAsPartiallyRefundedForTransaction(
                    transactionId
            );
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RefundResponse getById(UUID id) {

        return mapToResponse(
                repository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Refund not found: " + id
                                )
                        )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public RefundResponse getByGatewayRefundId(
            String gatewayRefundId) {

        return mapToResponse(
                repository.findByGatewayRefundId(gatewayRefundId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Refund not found: "
                                                + gatewayRefundId
                                )
                        )
        );
    }

    private RefundResponse mapToResponse(Refund entity) {

        return RefundResponse.builder()
                .id(entity.getId())
                .paymentId(entity.getPaymentId())
                .gatewayRefundId(entity.getGatewayRefundId())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .reason(entity.getReason())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}