package com.whoami.businessoperation.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.locato.constants.events.BusinessEvent;
import com.locato.constants.events.BusinessEventTopics;
import com.locato.constants.events.BusinessEventType;
import com.whoami.businessoperation.service.BusinessApplicationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class BusinessEventConsumer {

	
	private final BusinessApplicationService businessApplicationService;
    @KafkaListener(
            topics = BusinessEventTopics.BUSINESS_EVENTS,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeBusinessEvent(BusinessEvent event) {

        log.info(
                "Received business event: eventId={}, eventType={}, businessId={}, applicationId={}",
                event.getEventId(),
                event.getEventType(),
                event.getBusinessId(),
                event.getApplicationId()
        );
    }

   
    @KafkaListener(
            topics = BusinessEventTopics.BUSINESS_VERIFICATION,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeVerificationEvent(BusinessEvent event) {

        log.info(
                "Received verification event: eventId={}, eventType={}, businessId={}, applicationId={}",
                event.getEventId(),
                event.getEventType(),
                event.getBusinessId(),
                event.getApplicationId()
        );

        if (event.getEventType() == BusinessEventType.VERIFICATION_APPROVED) {

            log.info(
                    "Verification approved for businessId={}, applicationId={}",
                    event.getBusinessId(),
                    event.getApplicationId()
            );

            businessApplicationService.approveApplication(
                    event.getBusinessId(),
                    event.getPerformedBy()
            );
        }
    }
    

    @KafkaListener(
            topics = BusinessEventTopics.BUSINESS_CAPABILITY,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeCapabilityEvent(BusinessEvent event) {

        log.info(
                "Received capability event: eventId={}, eventType={}, businessId={}",
                event.getEventId(),
                event.getEventType(),
                event.getBusinessId()
        );
    }
}