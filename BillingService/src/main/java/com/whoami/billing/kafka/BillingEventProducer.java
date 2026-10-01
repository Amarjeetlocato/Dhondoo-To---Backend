package com.whoami.billing.kafka;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.locato.constants.events.billing.BillingCreatedEvent;
import com.locato.constants.events.billing.PaymentCompletedEvent;
import com.locato.constants.events.billing.PaymentFailedEvent;
import com.locato.constants.events.billing.RefundCompletedEvent;
import com.locato.constants.topics.KafkaTopics;
import com.whoami.billing.domain.entity.OutboxEvent;
import com.whoami.billing.service.OutboxService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BillingEventProducer {

    private final OutboxService outboxService;
    private final ObjectMapper objectMapper;

    public void publishBillingCreated(BillingCreatedEvent event) {
        saveOutboxEvent(
                "BillingTransaction",
                event.getTransactionId(),
                "BILLING_CREATED",
                KafkaTopics.BILLING_EVENTS,
                event
        );
    }

    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        saveOutboxEvent(
                "Payment",
                event.getTransactionId(),
                "PAYMENT_COMPLETED",
                KafkaTopics.BILLING_EVENTS,
                event
        );
    }

    public void publishPaymentFailed(PaymentFailedEvent event) {
        saveOutboxEvent(
                "Payment",
                event.getTransactionId(),
                "PAYMENT_FAILED",
                KafkaTopics.BILLING_EVENTS,
                event
        );
    }

    public void publishRefundCompleted(RefundCompletedEvent event) {
        saveOutboxEvent(
                "Refund",
                event.getTransactionId(),
                "REFUND_COMPLETED",
                KafkaTopics.BILLING_EVENTS,
                event
        );
    }

    private void saveOutboxEvent(
            String aggregateType,
            String aggregateId,
            String eventType,
            String topic,
            Object event
    ) {

        try {

            String payload =
                    objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent =
                    OutboxEvent.builder()
                            .aggregateType(aggregateType)
                            .aggregateId(aggregateId)
                            .eventType(eventType)
                            .topic(topic)
                            .payload(payload)
                            .build();

            outboxService.save(outboxEvent);

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "Failed to serialize " + eventType + " event",
                    e
            );
        }
    }
}