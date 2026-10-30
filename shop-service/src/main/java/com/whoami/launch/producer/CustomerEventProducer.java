package com.whoami.launch.producer;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.locato.constants.events.EventSources;
import com.locato.constants.events.EventVersions;
import com.locato.constants.events.customer.CustomerEventType;
import com.locato.constants.events.customer.CustomerProfileCreatedEvent;
import com.locato.constants.events.customer.CustomerProfileUpdatedEvent;
import com.locato.constants.events.customer.CustomerProfileDeletedEvent;
import com.locato.constants.topics.KafkaTopics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishCustomerProfileCreated(
            CustomerProfileCreatedEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                if (event.getEventId() == null) {
                    event.setEventId(UUID.randomUUID());
                }

                event.setEventType(
                        CustomerEventType.CUSTOMER_PROFILE_CREATED);
                event.setEventVersion(EventVersions.V1);
                event.setSource(EventSources.BUSINESS_SERVICE);

                if (event.getOccurredAt() == null) {
                    event.setOccurredAt(LocalDateTime.now());
                }

                kafkaTemplate.send(
                        KafkaTopics.CUSTOMER_EVENTS,
                        event.getCustomerId(),
                        event);
            } catch (Exception e) {
                log.error("Error publishing CustomerProfileCreatedEvent for customerId={}: {}", event.getCustomerId(), e.getMessage());
            }
        });
    }

    public void publishCustomerProfileUpdated(
            CustomerProfileUpdatedEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                if (event.getEventId() == null) {
                    event.setEventId(UUID.randomUUID());
                }

                event.setEventType(
                        CustomerEventType.CUSTOMER_PROFILE_UPDATED);
                event.setEventVersion(EventVersions.V1);
                event.setSource(EventSources.BUSINESS_SERVICE);

                if (event.getOccurredAt() == null) {
                    event.setOccurredAt(LocalDateTime.now());
                }

                kafkaTemplate.send(
                        KafkaTopics.CUSTOMER_EVENTS,
                        event.getCustomerId(),
                        event);
            } catch (Exception e) {
                log.error("Error publishing CustomerProfileUpdatedEvent for customerId={}: {}", event.getCustomerId(), e.getMessage());
            }
        });
    }

    public void publishCustomerProfileDeleted(
            CustomerProfileDeletedEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                if (event.getEventId() == null) {
                    event.setEventId(UUID.randomUUID());
                }

                event.setEventType(
                        CustomerEventType.CUSTOMER_PROFILE_DELETED);
                event.setEventVersion(EventVersions.V1);
                event.setSource(EventSources.BUSINESS_SERVICE);

                if (event.getOccurredAt() == null) {
                    event.setOccurredAt(LocalDateTime.now());
                }

                kafkaTemplate.send(
                        KafkaTopics.CUSTOMER_EVENTS,
                        event.getCustomerId(),
                        event);
            } catch (Exception e) {
                log.error("Error publishing CustomerProfileDeletedEvent for customerId={}: {}", event.getCustomerId(), e.getMessage());
            }
        });
    }
}
