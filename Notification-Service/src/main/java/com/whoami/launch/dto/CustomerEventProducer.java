package com.whoami.launch.dto;

import com.locato.constants.events.customer.CustomerProfileCreatedEvent;
import com.locato.constants.topics.KafkaTopics;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomerEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishCustomerProfileCreated(
            CustomerProfileCreatedEvent event) {

        kafkaTemplate.send(
                KafkaTopics.CUSTOMER_EVENTS,
                event.getCustomerId(),
                event
        );
    }
}