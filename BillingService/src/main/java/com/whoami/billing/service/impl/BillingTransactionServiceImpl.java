package com.whoami.billing.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.locato.dto.billing.event.BillingCreatedEvent;
import com.locato.topics.BillingTopics;
import com.whoami.billing.domain.entity.BillingTransaction;
import com.whoami.billing.domain.entity.BillingTransactionStatus;
import com.whoami.billing.domain.entity.OutboxEvent;
import com.whoami.billing.dto.request.CreateBillingRequest;
import com.whoami.billing.dto.response.BillingTransactionResponse;
import com.whoami.billing.repository.BillingTransactionRepository;
import com.whoami.billing.service.BillingTransactionService;
import com.whoami.billing.service.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

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
         * Idempotency protection:
         *
         * If the same billing request is received again,
         * return the already existing transaction instead of
         * creating another transaction.
         */
        return repository.findByIdempotencyKey(
                        request.getIdempotencyKey()
                )
                .map(this::mapToResponse)
                .orElseGet(() -> createNewTransaction(request));
    }

    private BillingTransactionResponse createNewTransaction(
            CreateBillingRequest request) {

        BillingTransaction transaction =
                BillingTransaction.builder()
                        .customerId(request.getCustomerId())
                        .referenceType(request.getReferenceType())
                        .referenceId(request.getReferenceId())
                        .purpose(request.getPurpose())
                        .amount(request.getAmount())
                        .currency(request.getCurrency())
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
                        .eventType("BILLING_CREATED")
                        .timestamp(LocalDateTime.now())
                        .source("BillingService")
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
                            .topic(BillingTopics.BILLING_CREATED)
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