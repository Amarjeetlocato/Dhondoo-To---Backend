package com.whoami.billing.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.locato.dto.billing.event.BillingCreatedEvent;
import com.locato.dto.billing.event.PaymentCompletedEvent;
import com.locato.dto.billing.event.PaymentFailedEvent;
import com.locato.dto.billing.event.RefundCompletedEvent;
import com.locato.topics.BillingTopics;
import com.whoami.billing.domain.entity.OutboxEvent;
import com.whoami.billing.service.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
                BillingTopics.BILLING_CREATED,
                event
        );
    }

    public void publishPaymentCompleted(PaymentCompletedEvent event) {
        saveOutboxEvent(
                "Payment",
                event.getTransactionId(),
                "PAYMENT_COMPLETED",
                BillingTopics.PAYMENT_COMPLETED,
                event
        );
    }

    public void publishPaymentFailed(PaymentFailedEvent event) {
        saveOutboxEvent(
                "Payment",
                event.getTransactionId(),
                "PAYMENT_FAILED",
                BillingTopics.PAYMENT_FAILED,
                event
        );
    }

    public void publishRefundCompleted(RefundCompletedEvent event) {
        saveOutboxEvent(
                "Refund",
                event.getTransactionId(),
                "REFUND_COMPLETED",
                BillingTopics.REFUND_COMPLETED,
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