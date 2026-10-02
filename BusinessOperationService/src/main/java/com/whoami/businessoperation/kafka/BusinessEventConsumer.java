package com.whoami.businessoperation.kafka;

import com.locato.constants.events.business.BusinessCreatedEvent;
import com.locato.constants.events.businessoperation.ApplicationCreatedEvent;
import com.locato.constants.events.businessoperation.ApplicationSubmittedEvent;
import com.locato.constants.events.businessoperation.BusinessOperationEventType;
import com.locato.constants.events.businessoperation.VerificationApprovedEvent;
import com.locato.constants.events.businessoperation.VerificationRejectedEvent;
import com.locato.constants.events.businessoperation.VerificationReuploadRequiredEvent;
import com.locato.constants.topics.KafkaTopics;
import com.whoami.businessoperation.service.BusinessApplicationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BusinessEventConsumer {

    private final BusinessApplicationService businessApplicationService;

    @KafkaListener(
            topics = KafkaTopics.BUSINESS_EVENTS,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeBusinessEvent(
            BusinessCreatedEvent event,
            Acknowledgment acknowledgment) {

        log.info(
                "Received business event: eventId={}, eventType={}, businessId={}, userId={}",
                event.getEventId(),
                event.getEventType(),
                event.getBusinessId(),
                event.getUserId()
        );

        acknowledgment.acknowledge();
    }

    @KafkaListener(
            topics = KafkaTopics.BUSINESS_OPERATION_EVENTS,
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeBusinessOperationEvent(
            Object event,
            Acknowledgment acknowledgment) {

        try {

            if (event instanceof ApplicationCreatedEvent applicationCreatedEvent) {

                log.info(
                        "Received application created event: eventId={}, eventType={}, businessId={}, applicationId={}",
                        applicationCreatedEvent.getEventId(),
                        applicationCreatedEvent.getEventType(),
                        applicationCreatedEvent.getBusinessId(),
                        applicationCreatedEvent.getApplicationId()
                );

            } else if (event instanceof ApplicationSubmittedEvent applicationSubmittedEvent) {

                log.info(
                        "Received application submitted event: eventId={}, eventType={}, businessId={}, applicationId={}",
                        applicationSubmittedEvent.getEventId(),
                        applicationSubmittedEvent.getEventType(),
                        applicationSubmittedEvent.getBusinessId(),
                        applicationSubmittedEvent.getApplicationId()
                );

            } else if (event instanceof VerificationApprovedEvent verificationApprovedEvent) {

                log.info(
                        "Received verification approved event: eventId={}, eventType={}, businessId={}, applicationId={}, verificationId={}",
                        verificationApprovedEvent.getEventId(),
                        verificationApprovedEvent.getEventType(),
                        verificationApprovedEvent.getBusinessId(),
                        verificationApprovedEvent.getApplicationId(),
                        verificationApprovedEvent.getVerificationId()
                );

                if (BusinessOperationEventType.VERIFICATION_APPROVED
                        .equals(verificationApprovedEvent.getEventType())) {

                    businessApplicationService.approveApplication(
                            verificationApprovedEvent.getBusinessId(),
                            verificationApprovedEvent.getPerformedBy()
                    );

                    log.info(
                            "Business application approved after verification: businessId={}, applicationId={}",
                            verificationApprovedEvent.getBusinessId(),
                            verificationApprovedEvent.getApplicationId()
                    );
                }

            } else if (event instanceof VerificationRejectedEvent verificationRejectedEvent) {

                log.info(
                        "Received verification rejected event: eventId={}, eventType={}, businessId={}, applicationId={}, verificationId={}",
                        verificationRejectedEvent.getEventId(),
                        verificationRejectedEvent.getEventType(),
                        verificationRejectedEvent.getBusinessId(),
                        verificationRejectedEvent.getApplicationId(),
                        verificationRejectedEvent.getVerificationId()
                );

                if (BusinessOperationEventType.VERIFICATION_REJECTED
                        .equals(verificationRejectedEvent.getEventType())) {

                    businessApplicationService.rejectApplication(
                            verificationRejectedEvent.getBusinessId(),
                            verificationRejectedEvent.getReason(),
                            verificationRejectedEvent.getPerformedBy()
                    );

                    log.info(
                            "Business application rejected after verification: businessId={}, applicationId={}",
                            verificationRejectedEvent.getBusinessId(),
                            verificationRejectedEvent.getApplicationId()
                    );
                }

            } else if (event instanceof VerificationReuploadRequiredEvent verificationReuploadRequiredEvent) {

                log.info(
                        "Received verification reupload required event: eventId={}, eventType={}, businessId={}, applicationId={}, verificationId={}",
                        verificationReuploadRequiredEvent.getEventId(),
                        verificationReuploadRequiredEvent.getEventType(),
                        verificationReuploadRequiredEvent.getBusinessId(),
                        verificationReuploadRequiredEvent.getApplicationId(),
                        verificationReuploadRequiredEvent.getVerificationId()
                );

                if (BusinessOperationEventType.VERIFICATION_REUPLOAD_REQUIRED
                        .equals(verificationReuploadRequiredEvent.getEventType())) {

                    businessApplicationService.requestApplicationReupload(
                            verificationReuploadRequiredEvent.getBusinessId(),
                            verificationReuploadRequiredEvent.getReason(),
                            verificationReuploadRequiredEvent.getPerformedBy()
                    );

                    log.info(
                            "Business application marked for reupload after verification: businessId={}, applicationId={}",
                            verificationReuploadRequiredEvent.getBusinessId(),
                            verificationReuploadRequiredEvent.getApplicationId()
                    );
                }

            } else {

                log.debug(
                        "Ignoring unsupported business operation event: {}",
                        event.getClass().getName()
                );
            }

            acknowledgment.acknowledge();

        } catch (Exception exception) {

            log.error(
                    "Failed to process business operation event",
                    exception
            );

            throw exception;
        }
    }
}
