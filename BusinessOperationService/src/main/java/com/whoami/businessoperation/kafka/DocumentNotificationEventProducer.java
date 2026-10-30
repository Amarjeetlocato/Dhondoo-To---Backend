package com.whoami.businessoperation.kafka;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.locato.constants.events.EventSources;
import com.locato.constants.events.EventVersions;
import com.locato.constants.events.businessoperation.BusinessOperationEventType;
import com.locato.constants.events.businessoperation.DocumentApprovedEvent;
import com.locato.constants.events.businessoperation.DocumentRejectedEvent;
import com.locato.constants.events.businessoperation.DocumentReuploadRequiredEvent;
import com.locato.constants.events.businessoperation.DocumentUploadedEvent;
import com.locato.constants.topics.KafkaTopics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentNotificationEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishDocumentUploaded(
            String businessId,
            String applicationId,
            String documentId,
            String userId,
            String documentType,
            String documentName,
            String description,
            String correlationId) {

        DocumentUploadedEvent event = DocumentUploadedEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType(BusinessOperationEventType.DOCUMENT_UPLOADED)
                .eventVersion(EventVersions.V1)
                .source(EventSources.BUSINESS_OPERATION_SERVICE)
                .occurredAt(LocalDateTime.now())
                .correlationId(correlationId)
                .businessId(businessId)
                .applicationId(applicationId)
                .documentId(documentId)
                .userId(userId)
                .documentType(documentType)
                .documentName(documentName)
                .description(description)
                .build();

        publish(businessId, event, "DOCUMENT_UPLOADED");
    }

    public void publishDocumentApproved(
            String businessId,
            String applicationId,
            String documentId,
            String performedBy,
            String performedByRole,
            String description,
            String correlationId) {

        DocumentApprovedEvent event = DocumentApprovedEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType(BusinessOperationEventType.DOCUMENT_APPROVED)
                .eventVersion(EventVersions.V1)
                .source(EventSources.BUSINESS_OPERATION_SERVICE)
                .occurredAt(LocalDateTime.now())
                .correlationId(correlationId)
                .businessId(businessId)
                .applicationId(applicationId)
                .documentId(documentId)
                .performedBy(performedBy)
                .performedByRole(performedByRole)
                .description(description)
                .build();

        publish(businessId, event, "DOCUMENT_APPROVED");
    }

    public void publishDocumentRejected(
            String businessId,
            String applicationId,
            String documentId,
            String performedBy,
            String performedByRole,
            String reason,
            String correlationId) {

        DocumentRejectedEvent event = DocumentRejectedEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType(BusinessOperationEventType.DOCUMENT_REJECTED)
                .eventVersion(EventVersions.V1)
                .source(EventSources.BUSINESS_OPERATION_SERVICE)
                .occurredAt(LocalDateTime.now())
                .correlationId(correlationId)
                .businessId(businessId)
                .applicationId(applicationId)
                .documentId(documentId)
                .performedBy(performedBy)
                .performedByRole(performedByRole)
                .reason(reason)
                .build();

        publish(businessId, event, "DOCUMENT_REJECTED");
    }

    public void publishDocumentReuploadRequired(
            String businessId,
            String applicationId,
            String documentId,
            String performedBy,
            String performedByRole,
            String reason,
            String correlationId) {

        DocumentReuploadRequiredEvent event =
                DocumentReuploadRequiredEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(
                                BusinessOperationEventType.DOCUMENT_REUPLOAD_REQUIRED)
                        .eventVersion(EventVersions.V1)
                        .source(EventSources.BUSINESS_OPERATION_SERVICE)
                        .occurredAt(LocalDateTime.now())
                        .correlationId(correlationId)
                        .businessId(businessId)
                        .applicationId(applicationId)
                        .documentId(documentId)
                        .performedBy(performedBy)
                        .performedByRole(performedByRole)
                        .reason(reason)
                        .build();

        publish(businessId, event, "DOCUMENT_REUPLOAD_REQUIRED");
    }

    private void publish(String key, Object event, String eventName) {
        if (!StringUtils.hasText(key)) {
            throw new IllegalArgumentException(
                    "businessId is required as the Kafka message key");
        }

        kafkaTemplate.send(
                KafkaTopics.BUSINESS_OPERATION_EVENTS,
                key,
                event
        ).whenComplete((result, exception) -> {
            if (exception != null) {
                log.error(
                        "Failed to publish {} event for businessId={}",
                        eventName,
                        key,
                        exception
                );
            } else {
                log.debug(
                        "Published {} event for businessId={}, partition={}, offset={}",
                        eventName,
                        key,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
            }
        });
    }
}