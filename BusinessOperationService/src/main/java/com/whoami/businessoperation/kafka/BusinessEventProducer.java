package com.whoami.businessoperation.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.locato.constants.events.BusinessEvent;
import com.locato.constants.events.BusinessEventTopics;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BusinessEventProducer {

    private final KafkaTemplate<String, BusinessEvent> kafkaTemplate;

    public void publishBusinessEvent(BusinessEvent event) {
        kafkaTemplate.send(
                BusinessEventTopics.BUSINESS_EVENTS,
                event.getBusinessId().toString(),
                event
        );
    }

    public void publishVerificationEvent(BusinessEvent event) {
        kafkaTemplate.send(
                BusinessEventTopics.BUSINESS_VERIFICATION,
                event.getBusinessId().toString(),
                event
        );
    }

    public void publishCapabilityEvent(BusinessEvent event) {
        kafkaTemplate.send(
                BusinessEventTopics.BUSINESS_CAPABILITY,
                event.getBusinessId().toString(),
                event
        );
    }
}