package com.whoami.billing.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.locato.constants.events.billing.BillingRequestedEvent;
import com.locato.constants.topics.KafkaTopics;
import com.whoami.billing.dto.request.CreateBillingRequest;
import com.whoami.billing.dto.response.BillingTransactionResponse;
import com.whoami.billing.service.BillingTransactionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillingEventConsumer {

    private final BillingTransactionService billingTransactionService;

    @KafkaListener(
            topics = KafkaTopics.BILLING_EVENTS,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeBillingRequested(BillingRequestedEvent event) {

        log.info(
                "Billing request received: eventId={}, referenceType={}, referenceId={}, customerId={}, amount={}, idempotencyKey={}",
                event.getEventId(),
                event.getReferenceType(),
                event.getReferenceId(),
                event.getCustomerId(),
                event.getAmount(),
                event.getIdempotencyKey()
        );

        CreateBillingRequest request = CreateBillingRequest.builder()
                .customerId(event.getCustomerId())
                .referenceType(event.getReferenceType())
                .referenceId(event.getReferenceId())
                .purpose(event.getPurpose())
                .amount(event.getAmount())
                .currency(event.getCurrency())
                .idempotencyKey(event.getIdempotencyKey())
                .build();

        try {

            BillingTransactionResponse response =
                    billingTransactionService.create(request);

            log.info(
                    "Billing transaction created: transactionId={}, status={}",
                    response.getTransactionId(),
                    response.getStatus()
            );

        } catch (IllegalStateException e) {

            log.info(
                    "Duplicate billing request ignored: idempotencyKey={}, reason={}",
                    event.getIdempotencyKey(),
                    e.getMessage()
            );
        }
    }
}