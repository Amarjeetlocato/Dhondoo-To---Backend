package com.whoami.billing.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.locato.constants.events.EventSources;
import com.locato.constants.events.EventVersions;
import com.locato.constants.events.billing.BillingCreatedEvent;
import com.locato.constants.events.billing.BillingEventType;
import com.locato.constants.topics.KafkaTopics;
import com.whoami.billing.domain.entity.BillingTransaction;
import com.whoami.billing.domain.entity.BillingTransactionStatus;
import com.whoami.billing.domain.entity.OutboxEvent;
import com.whoami.billing.dto.request.CreateBillingRequest;
import com.whoami.billing.dto.response.BillingTransactionResponse;
import com.whoami.billing.repository.BillingTransactionRepository;
import com.whoami.billing.service.BillingTransactionService;
import com.whoami.billing.service.OutboxService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BillingTransactionServiceImpl
        implements BillingTransactionService {

    private final BillingTransactionRepository repository;
    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    @Override
    public BillingTransactionResponse create(
            CreateBillingRequest request) {

        /*
         * Idempotency & Reuse protection:
         *
         * 1. Check if transaction with identical idempotencyKey already exists.
         */
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().isBlank()) {
            var existingByIdempotency = repository.findByIdempotencyKey(request.getIdempotencyKey());
            if (existingByIdempotency.isPresent()) {
                return mapToResponse(existingByIdempotency.get());
            }
        }

        /*
         * 2. If a PAID transaction already exists for this business/reference, return it.
         */
        if (request.getReferenceType() != null && request.getReferenceId() != null) {
            var existingPaid = repository.findFirstByReferenceTypeAndReferenceIdAndStatus(
                    request.getReferenceType(),
                    request.getReferenceId(),
                    BillingTransactionStatus.PAID
            );
            if (existingPaid.isPresent()) {
                return mapToResponse(existingPaid.get());
            }
        }

        /*
         * 3. Check if an active UNPAID (CREATED) transaction already exists for this
         *    business/reference and purpose (e.g. user retrying payment or returning to page).
         */
        if (request.getReferenceType() != null && request.getReferenceId() != null && request.getPurpose() != null) {
            var existingActive = repository.findFirstByReferenceTypeAndReferenceIdAndPurposeAndStatus(
                    request.getReferenceType(),
                    request.getReferenceId(),
                    request.getPurpose(),
                    BillingTransactionStatus.CREATED
            );
            if (existingActive.isPresent()) {
                return mapToResponse(existingActive.get());
            }
        }

        return createNewTransaction(request);
    }

    public static final BigDecimal VERIFICATION_FEE = new BigDecimal("149.00");
    public static final BigDecimal SUBSCRIPTION_FEE = new BigDecimal("299.00");
    public static final BigDecimal COMBO_BUNDLE_FEE = new BigDecimal("399.00");
    public static final BigDecimal COMBO_REFUND_AMOUNT = new BigDecimal("250.00");

    private BigDecimal resolveServerEnforcedAmount(
            String purpose,
            String referenceType,
            BigDecimal clientAmount) {

        String p = purpose != null ? purpose.toUpperCase().trim() : "";
        String r = referenceType != null ? referenceType.toUpperCase().trim() : "";

        // 1. Special Combo Bundle Offer: Verification + 1-Month Subscription = ₹399.00
        if (p.contains("COMBO") || r.contains("COMBO")) {
            return COMBO_BUNDLE_FEE;
        }

        // 2. Standalone Verification Fee = ₹149.00
        if (p.contains("VERIFICATION") || r.contains("VERIFICATION")) {
            return VERIFICATION_FEE;
        }

        // 3. Standalone Subscription Fee = ₹299.00
        if (p.contains("SUBSCRIPTION") || r.contains("SUBSCRIPTION")) {
            return SUBSCRIPTION_FEE;
        }

        // 4. Default: ensure positive amount
        if (clientAmount == null || clientAmount.signum() <= 0) {
            throw new IllegalArgumentException("Billing transaction amount must be greater than zero");
        }

        return clientAmount;
    }

    private BillingTransactionResponse createNewTransaction(
            CreateBillingRequest request) {

        BigDecimal serverEnforcedAmount = resolveServerEnforcedAmount(
                request.getPurpose(),
                request.getReferenceType(),
                request.getAmount()
        );

        BillingTransaction transaction =
                BillingTransaction.builder()
                        .customerId(request.getCustomerId())
                        .referenceType(request.getReferenceType())
                        .referenceId(request.getReferenceId())
                        .purpose(request.getPurpose())
                        .amount(serverEnforcedAmount)
                        .currency("INR")
                        .status(BillingTransactionStatus.CREATED)
                        .idempotencyKey(request.getIdempotencyKey())
                        .build();

        BillingTransaction saved =
                repository.save(transaction);

        /*
         * Create the domain event.
         */
        BillingCreatedEvent event =
                BillingCreatedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(BillingEventType.BILLING_CREATED)
                        .eventVersion(EventVersions.V1)
                        .source(EventSources.BILLING_SERVICE)
                        .occurredAt(LocalDateTime.now())
                        .correlationId(null)
                        .transactionId(saved.getTransactionId())
                        .customerId(saved.getCustomerId())
                        .referenceType(saved.getReferenceType())
                        .referenceId(saved.getReferenceId())
                        .purpose(saved.getPurpose())
                        .amount(saved.getAmount())
                        .currency(saved.getCurrency())
                        .status(saved.getStatus().name())
                        .build();

        /*
         * Outbox Pattern:
         *
         * BillingTransaction and OutboxEvent are saved
         * inside the same database transaction.
         *
         * Kafka is NOT called directly here.
         */
        try {

            String payload =
                    objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent =
                    OutboxEvent.builder()
                            .aggregateType("BillingTransaction")
                            .aggregateId(saved.getTransactionId())
                            .eventType("BILLING_CREATED")
                            .topic(KafkaTopics.BILLING_EVENTS)
                            .payload(payload)
                            .build();

            outboxService.save(outboxEvent);

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "Failed to serialize BILLING_CREATED event",
                    e
            );
        }

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BillingTransactionResponse getById(UUID id) {

        return mapToResponse(
                repository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Billing transaction not found: "
                                                + id
                                )
                        )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BillingTransactionResponse getByTransactionId(
            String transactionId) {

        return mapToResponse(
                repository.findByTransactionId(transactionId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Billing transaction not found: "
                                                + transactionId
                                )
                        )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BillingTransactionResponse getByIdempotencyKey(
            String idempotencyKey) {

        return mapToResponse(
                repository.findByIdempotencyKey(idempotencyKey)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Billing transaction not found"
                                )
                        )
        );
    }

    @Override
    public void markAsPaid(UUID transactionId) {

        BillingTransaction transaction =
                findTransaction(transactionId);

        transaction.setStatus(
                BillingTransactionStatus.PAID
        );

        repository.save(transaction);
    }

    @Override
    public void markAsFailed(UUID transactionId) {

        BillingTransaction transaction =
                findTransaction(transactionId);

        transaction.setStatus(
                BillingTransactionStatus.FAILED
        );

        repository.save(transaction);
    }

    @Override
    public void markAsRefunded(UUID transactionId) {

        BillingTransaction transaction =
                findTransaction(transactionId);

        transaction.setStatus(
                BillingTransactionStatus.REFUNDED
        );

        repository.save(transaction);
    }

    @Override
    public void markAsPartiallyRefunded(
            UUID transactionId) {

        BillingTransaction transaction =
                findTransaction(transactionId);

        transaction.setStatus(
                BillingTransactionStatus.PARTIALLY_REFUNDED
        );

        repository.save(transaction);
    }

    private BillingTransaction findTransaction(
            UUID transactionId) {

        return repository.findById(transactionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Billing transaction not found: "
                                        + transactionId
                        )
                );
    }

    private BillingTransactionResponse mapToResponse(
            BillingTransaction entity) {

        return BillingTransactionResponse.builder()
                .id(entity.getId())
                .transactionId(entity.getTransactionId())
                .customerId(entity.getCustomerId())
                .referenceType(entity.getReferenceType())
                .referenceId(entity.getReferenceId())
                .purpose(entity.getPurpose())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}