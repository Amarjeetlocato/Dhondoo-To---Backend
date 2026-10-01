package com.whoami.businessoperation.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.locato.constants.events.business.BusinessCreatedEvent;
import com.locato.constants.events.businessoperation.ApplicationCreatedEvent;
import com.locato.constants.events.businessoperation.BusinessOperationEventType;
import com.locato.constants.topics.KafkaTopics;
import com.whoami.businessoperation.service.BusinessApplicationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BusinessEventConsumer {

    private final BusinessApplicationService businessApplicationService;

    @KafkaListener(
            topics = KafkaTopics.BUSINESS_EVENTS,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeBusinessEvent(BusinessCreatedEvent event) {

        log.info(
                "Received business event: eventId={}, eventType={}, businessId={}, userId={}",
                event.getEventId(),
                event.getEventType(),
                event.getBusinessId(),
                event.getUserId()
        );
    }

    @KafkaListener(
            topics = KafkaTopics.BUSINESS_OPERATION_EVENTS,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeBusinessOperationEvent(
            ApplicationCreatedEvent event) {

        log.info(
                "Received business operation event: eventId={}, eventType={}, businessId={}, applicationId={}",
                event.getEventId(),
                event.getEventType(),
                event.getBusinessId(),
                event.getApplicationId()
        );

        if (BusinessOperationEventType.APPLICATION_CREATED.name()
                .equals(event.getEventType())) {

            log.info(
                    "Business application created: businessId={}, applicationId={}",
                    event.getBusinessId(),
                    event.getApplicationId()
            );
        }
    }
}