package com.whoami.billing.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.locato.dto.billing.event.BillingCreatedEvent;
import com.locato.dto.billing.event.RecurringPaymentRequestedEvent;
import com.locato.topics.BillingTopics;
import com.whoami.billing.domain.entity.BillingPlan;
import com.whoami.billing.domain.entity.BillingTransaction;
import com.whoami.billing.domain.entity.BillingTransactionStatus;
import com.whoami.billing.domain.entity.OutboxEvent;
import com.whoami.billing.domain.entity.Subscription;
import com.whoami.billing.domain.entity.SubscriptionStatus;
import com.whoami.billing.repository.BillingPlanRepository;
import com.whoami.billing.repository.BillingTransactionRepository;
import com.whoami.billing.repository.OutboxEventRepository;
import com.whoami.billing.repository.SubscriptionRepository;
import com.whoami.billing.service.RecurringBillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecurringBillingServiceImpl
        implements RecurringBillingService {

    private static final ZoneId BUSINESS_ZONE =
            ZoneId.of("Asia/Kolkata");

    private final SubscriptionRepository subscriptionRepository;
    private final BillingPlanRepository billingPlanRepository;
    private final BillingTransactionRepository billingTransactionRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void processDueSubscriptions() {

        LocalDate today =
                LocalDate.now(BUSINESS_ZONE);

        processDueSubscriptions(today);
    }

    @Override
    @Transactional
    public void processDueSubscriptions(LocalDate asOfDate) {

        List<Subscription> subscriptions =
                subscriptionRepository
                        .findByStatusAndNextBillingDateLessThanEqual(
                                SubscriptionStatus.ACTIVE,
                                asOfDate
                        );

        log.info(
                "DEBUG recurring query: asOfDate={}, foundSubscriptions={}",
                asOfDate,
                subscriptions.size()
        );

        if (subscriptions.isEmpty()) {

            log.info(
                    "No subscriptions due for recurring billing asOfDate={}",
                    asOfDate
            );

            return;
        }

        log.info(
                "Found {} subscriptions due for recurring billing asOfDate={}",
                subscriptions.size(),
                asOfDate
        );

        for (Subscription subscription : subscriptions) {
            processSubscription(subscription);
        }
    }

    private void processSubscription(
            Subscription subscription) {

        BillingPlan plan =
                billingPlanRepository.findById(
                        subscription.getPlanId()
                ).orElse(null);

        if (plan == null || !plan.isActive()) {

            log.warn(
                    "Skipping subscription {} because billing plan is missing or inactive",
                    subscription.getId()
            );

            return;
        }

        if (plan.getBillingCycle().name().equals("ONE_TIME")) {

            log.warn(
                    "Skipping one-time subscription {} from recurring billing",
                    subscription.getId()
            );

            return;
        }

        LocalDate billingDate =
                subscription.getNextBillingDate();

        if (billingDate == null) {

            log.warn(
                    "Skipping subscription {} because next billing date is null",
                    subscription.getId()
            );

            return;
        }

        String idempotencyKey =
                "SUBSCRIPTION_RENEWAL:"
                        + subscription.getId()
                        + ":"
                        + billingDate;

        if (billingTransactionRepository
                .findByIdempotencyKey(idempotencyKey)
                .isPresent()) {

            log.info(
                    "Renewal billing already exists: subscriptionId={}, billingDate={}",
                    subscription.getId(),
                    billingDate
            );

            return;
        }

        BillingTransaction transaction =
                BillingTransaction.builder()
                        .customerId(subscription.getCustomerId())
                        .referenceType("SUBSCRIPTION")
                        .referenceId(
                                subscription.getId().toString()
                        )
                        .purpose("SUBSCRIPTION_RENEWAL")
                        .amount(plan.getAmount())
                        .currency(plan.getCurrency())
                        .status(BillingTransactionStatus.CREATED)
                        .idempotencyKey(idempotencyKey)
                        .build();

        BillingTransaction saved =
                billingTransactionRepository.save(transaction);

        /*
         * ---------------------------------------------------------
         * 1. BILLING_CREATED EVENT
         * ---------------------------------------------------------
         */

        BillingCreatedEvent billingCreatedEvent =
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
         * ---------------------------------------------------------
         * 2. RECURRING_PAYMENT_REQUESTED EVENT
         * ---------------------------------------------------------
         */

        RecurringPaymentRequestedEvent paymentRequestedEvent =
                RecurringPaymentRequestedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType("RECURRING_PAYMENT_REQUESTED")
                        .timestamp(LocalDateTime.now())
                        .source("BillingService")
                        .subscriptionId(subscription.getId())
                        .transactionId(saved.getTransactionId())
                        .customerId(saved.getCustomerId())
                        .amount(saved.getAmount())
                        .currency(saved.getCurrency())
                        .purpose(saved.getPurpose())
                        .gateway(subscription.getGateway().name())
                        .build();
        try {

            /*
             * -----------------------------------------------------
             * BILLING_CREATED OUTBOX
             * -----------------------------------------------------
             */

            String billingCreatedPayload =
                    objectMapper.writeValueAsString(
                            billingCreatedEvent
                    );

            OutboxEvent billingCreatedOutbox =
                    OutboxEvent.builder()
                            .aggregateType("BillingTransaction")
                            .aggregateId(
                                    saved.getTransactionId()
                            )
                            .eventType("BILLING_CREATED")
                            .topic(
                                    BillingTopics.BILLING_CREATED
                            )
                            .payload(billingCreatedPayload)
                            .build();

            outboxEventRepository.save(
                    billingCreatedOutbox
            );

            /*
             * -----------------------------------------------------
             * RECURRING_PAYMENT_REQUESTED OUTBOX
             * -----------------------------------------------------
             */

            String paymentRequestedPayload =
                    objectMapper.writeValueAsString(
                            paymentRequestedEvent
                    );

            OutboxEvent paymentRequestedOutbox =
                    OutboxEvent.builder()
                            .aggregateType("Subscription")
                            .aggregateId(
                                    subscription.getId().toString()
                            )
                            .eventType(
                                    "RECURRING_PAYMENT_REQUESTED"
                            )
                            .topic(
                                    BillingTopics.RECURRING_PAYMENT_REQUESTED
                            )
                            .payload(paymentRequestedPayload)
                            .build();

            outboxEventRepository.save(
                    paymentRequestedOutbox
            );

            log.info(
                    "Recurring billing transaction created: " +
                            "subscriptionId={}, transactionId={}, " +
                            "amount={}, currency={}, billingDate={}",
                    subscription.getId(),
                    saved.getTransactionId(),
                    saved.getAmount(),
                    saved.getCurrency(),
                    billingDate
            );

            log.info(
                    "Recurring payment requested event created: " +
                            "subscriptionId={}, transactionId={}",
                    subscription.getId(),
                    saved.getTransactionId()
            );

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "Failed to serialize recurring billing events",
                    e
            );
        }
    }
}