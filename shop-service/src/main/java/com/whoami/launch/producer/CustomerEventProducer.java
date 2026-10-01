package com.whoami.launch.producer;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.locato.constants.events.EventSources;
import com.locato.constants.events.EventVersions;
import com.locato.constants.events.customer.CustomerEventType;
import com.locato.constants.events.customer.CustomerProfileCreatedEvent;
import com.locato.constants.topics.KafkaTopics;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomerEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishCustomerProfileCreated(
            CustomerProfileCreatedEvent event) {

        if (event.getEventId() == null) {
            event.setEventId(UUID.randomUUID());
        }

        event.setEventType(
                CustomerEventType.CUSTOMER_PROFILE_CREATED
        );

        event.setEventVersion(EventVersions.V1);

        event.setSource(EventSources.BUSINESS_SERVICE);

        if (event.getOccurredAt() == null) {
            event.setOccurredAt(LocalDateTime.now());
        }

        kafkaTemplate.send(
                KafkaTopics.CUSTOMER_EVENTS,
                event.getCustomerId(),
                event
        );
    }
}