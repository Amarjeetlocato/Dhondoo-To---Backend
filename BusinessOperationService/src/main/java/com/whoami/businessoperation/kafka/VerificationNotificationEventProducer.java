package com.whoami.businessoperation.kafka;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.locato.constants.events.EventSources;
import com.locato.constants.events.EventVersions;
import com.locato.constants.events.businessoperation.BusinessOperationEventType;
import com.locato.constants.events.businessoperation.VerificationApprovedEvent;
import com.locato.constants.events.businessoperation.VerificationRejectedEvent;
import com.locato.constants.events.businessoperation.VerificationReuploadRequiredEvent;
import com.locato.constants.events.businessoperation.VerificationStartedEvent;
import com.locato.constants.events.businessoperation.VerificationSubmittedEvent;
import com.locato.constants.topics.KafkaTopics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class VerificationNotificationEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishVerificationStarted(
            String businessId,
            String applicationId,
            String verificationId,
            String performedBy,
            String performedByRole,
            String description) {

        VerificationStartedEvent event =
                VerificationStartedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(
                                BusinessOperationEventType.VERIFICATION_STARTED)
                        .eventVersion(EventVersions.V1)
                        .source(EventSources.BUSINESS_OPERATION_SERVICE)
                        .occurredAt(LocalDateTime.now())
                        .businessId(businessId)
                        .applicationId(applicationId)
                        .verificationId(verificationId)
                        .performedBy(performedBy)
                        .performedByRole(performedByRole)
                        .description(description)
                        .build();

        publish(businessId, event);
    }

    public void publishVerificationSubmitted(
            String businessId,
            String applicationId,
            String verificationId,
            String performedBy,
            String performedByRole,
            String description) {

        VerificationSubmittedEvent event =
                VerificationSubmittedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(
                                BusinessOperationEventType.VERIFICATION_SUBMITTED)
                        .eventVersion(EventVersions.V1)
                        .source(EventSources.BUSINESS_OPERATION_SERVICE)
                        .occurredAt(LocalDateTime.now())
                        .businessId(businessId)
                        .applicationId(applicationId)
                        .verificationId(verificationId)
                        .performedBy(performedBy)
                        .performedByRole(performedByRole)
                        .description(description)
                        .build();

        publish(businessId, event);
    }

    public void publishVerificationApproved(
            String businessId,
            String applicationId,
            String verificationId,
            String performedBy,
            String performedByRole,
            String description) {

        VerificationApprovedEvent event =
                VerificationApprovedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(
                                BusinessOperationEventType.VERIFICATION_APPROVED)
                        .eventVersion(EventVersions.V1)
                        .source(EventSources.BUSINESS_OPERATION_SERVICE)
                        .occurredAt(LocalDateTime.now())
                        .businessId(businessId)
                        .applicationId(applicationId)
                        .verificationId(verificationId)
                        .performedBy(performedBy)
                        .performedByRole(performedByRole)
                        .description(description)
                        .build();

        publish(businessId, event);
    }

    public void publishVerificationRejected(
            String businessId,
            String applicationId,
            String verificationId,
            String performedBy,
            String performedByRole,
            String reason) {

        VerificationRejectedEvent event =
                VerificationRejectedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(
                                BusinessOperationEventType.VERIFICATION_REJECTED)
                        .eventVersion(EventVersions.V1)
                        .source(EventSources.BUSINESS_OPERATION_SERVICE)
                        .occurredAt(LocalDateTime.now())
                        .businessId(businessId)
                        .applicationId(applicationId)
                        .verificationId(verificationId)
                        .performedBy(performedBy)
                        .performedByRole(performedByRole)
                        .reason(reason)
                        .build();

        publish(businessId, event);
    }

    public void publishVerificationReuploadRequired(
            String businessId,
            String applicationId,
            String verificationId,
            String performedBy,
            String performedByRole,
            String reason) {

        VerificationReuploadRequiredEvent event =
                VerificationReuploadRequiredEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(
                                BusinessOperationEventType.VERIFICATION_REUPLOAD_REQUIRED)
                        .eventVersion(EventVersions.V1)
                        .source(EventSources.BUSINESS_OPERATION_SERVICE)
                        .occurredAt(LocalDateTime.now())
                        .businessId(businessId)
                        .applicationId(applicationId)
                        .verificationId(verificationId)
                        .performedBy(performedBy)
                        .performedByRole(performedByRole)
                        .reason(reason)
                        .build();

        publish(businessId, event);
    }

    private void publish(String businessId, Object event) {
        try {
            kafkaTemplate.send(
                    KafkaTopics.BUSINESS_OPERATION_EVENTS,
                    businessId,
                    event
            ).whenComplete((result, exception) -> {
                if (exception != null) {
                    log.error("Failed to publish Kafka verification event for businessId={}: {}", businessId, exception.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("Error sending Kafka verification event for businessId={}: {}", businessId, e.getMessage());
        }
    }
}