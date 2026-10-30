package com.whoami.launch.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.locato.constants.topics.KafkaTopics;
import com.locato.dto.PaymentEvent;
import com.whoami.launch.service.KafkaEventPublisherService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventConsumer {

    private final KafkaEventPublisherService eventPublisherService;

    @KafkaListener(
            topics = {KafkaTopics.PAYMENT_EVENTS, KafkaTopics.BILLING_EVENTS},
            groupId = "superuser-group"
    )
    public void consumePaymentEvent(ConsumerRecord<String, Object> record) {
        Object value = record.value();
        log.info("SuperUser received payment/billing event: {}", value);

        if (value instanceof PaymentEvent payment) {
            log.info("Processing PaymentEvent -> ID: {}, Amount: {}, Status: {}, Business: {}",
                    payment.getPaymentId(), payment.getAmount(), payment.getStatus(), payment.getBusinessId());

            if ("SUCCESS".equalsIgnoreCase(payment.getStatus()) && payment.getUserId() != null) {
                eventPublisherService.sendNotification(
                        payment.getUserId(),
                        "Payment Successful",
                        "Your payment of " + payment.getCurrency() + " " + payment.getAmount() + " was successful.",
                        payment.getPaymentId(),
                        "PAYMENT",
                        "TRANSACTION",
                        "/account/orders"
                );
            }
        }
    }
}
