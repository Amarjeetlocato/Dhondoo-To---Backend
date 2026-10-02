package com.whoami.businessoperation.kafka;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.locato.constants.events.EventSources;
import com.locato.constants.events.EventVersions;
import com.locato.constants.events.businessoperation.ApplicationCreatedEvent;
import com.locato.constants.events.businessoperation.ApplicationSubmittedEvent;
import com.locato.constants.events.businessoperation.BusinessActivatedEvent;
import com.locato.constants.events.businessoperation.BusinessApprovedEvent;
import com.locato.constants.events.businessoperation.BusinessOperationEventType;
import com.locato.constants.events.businessoperation.BusinessRejectedEvent;
import com.locato.constants.events.businessoperation.CapabilityDisabledEvent;
import com.locato.constants.events.businessoperation.CapabilityEnabledEvent;
import com.locato.constants.events.businessoperation.CapabilitySuspendedEvent;
import com.locato.constants.events.businessoperation.DocumentApprovedEvent;
import com.locato.constants.events.businessoperation.DocumentRejectedEvent;
import com.locato.constants.events.businessoperation.DocumentReuploadRequiredEvent;
import com.locato.constants.events.businessoperation.DocumentUploadedEvent;
import com.locato.constants.events.businessoperation.VerificationApprovedEvent;
import com.locato.constants.events.businessoperation.VerificationRejectedEvent;
import com.locato.constants.events.businessoperation.VerificationReuploadRequiredEvent;
import com.locato.constants.events.businessoperation.VerificationStartedEvent;
import com.locato.constants.events.businessoperation.VerificationSubmittedEvent;
import com.locato.constants.topics.KafkaTopics;
import com.whoami.businessoperation.domain.entity.BusinessApplication;
import com.whoami.businessoperation.domain.entity.BusinessCapability;
import com.whoami.businessoperation.domain.entity.BusinessDocument;
import com.whoami.businessoperation.domain.entity.BusinessVerification;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BusinessEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishApplicationCreatedEvent(
            ApplicationCreatedEvent event) {

        publish(
                event.getBusinessId(),
                event
        );
    }

    public void publishBusinessOperationEvent(
            BusinessOperationEventType eventType,
            BusinessApplication application,
            String performedBy,
            String performedByRole,
            String description,
            String reason) {

        switch (eventType) {

            case APPLICATION_CREATED -> {

                ApplicationCreatedEvent event =
                        ApplicationCreatedEvent.builder()
                                .eventId(UUID.randomUUID())
                                .eventType(eventType)
                                .eventVersion(EventVersions.V1)
                                .source(
                                        EventSources
                                                .BUSINESS_OPERATION_SERVICE
                                )
                                .occurredAt(LocalDateTime.now())
                                .correlationId(null)
                                .businessId(
                                        application.getBusinessId()
                                )
                                .applicationId(
                                        application.getApplicationId()
                                )
                                .userId(
                                        application.getOwnerUserId()
                                )
                                .description(description)
                                .build();

                publish(
                        event.getBusinessId(),
                        event
                );
            }

            case APPLICATION_SUBMITTED -> {

                ApplicationSubmittedEvent event =
                        ApplicationSubmittedEvent.builder()
                                .eventId(UUID.randomUUID())
                                .eventType(eventType)
                                .eventVersion(EventVersions.V1)
                                .source(
                                        EventSources
                                                .BUSINESS_OPERATION_SERVICE
                                )
                                .occurredAt(LocalDateTime.now())
                                .correlationId(null)
                                .businessId(
                                        application.getBusinessId()
                                )
                                .applicationId(
                                        application.getApplicationId()
                                )
                                .userId(performedBy)
                                .description(description)
                                .build();

                publish(
                        event.getBusinessId(),
                        event
                );
            }

            case BUSINESS_REJECTED -> {

                BusinessRejectedEvent event =
                        BusinessRejectedEvent.builder()
                                .eventId(UUID.randomUUID())
                                .eventType(eventType)
                                .eventVersion(EventVersions.V1)
                                .source(
                                        EventSources
                                                .BUSINESS_OPERATION_SERVICE
                                )
                                .occurredAt(LocalDateTime.now())
                                .correlationId(null)
                                .businessId(
                                        application.getBusinessId()
                                )
                                .applicationId(
                                        application.getApplicationId()
                                )
                                .performedBy(performedBy)
                                .performedByRole(performedByRole)
                                .reason(reason)
                                .build();

                publish(
                        event.getBusinessId(),
                        event
                );
            }

            case BUSINESS_APPROVED -> {

                BusinessApprovedEvent event =
                        BusinessApprovedEvent.builder()
                                .eventId(UUID.randomUUID())
                                .eventType(eventType)
                                .eventVersion(EventVersions.V1)
                                .source(
                                        EventSources
                                                .BUSINESS_OPERATION_SERVICE
                                )
                                .occurredAt(LocalDateTime.now())
                                .correlationId(null)
                                .businessId(
                                        application.getBusinessId()
                                )
                                .applicationId(
                                        application.getApplicationId()
                                )
                                .performedBy(performedBy)
                                .performedByRole(performedByRole)
                                .description(description)
                                .build();

                publish(
                        event.getBusinessId(),
                        event
                );
            }

            case BUSINESS_ACTIVATED -> {

                BusinessActivatedEvent event =
                        BusinessActivatedEvent.builder()
                                .eventId(UUID.randomUUID())
                                .eventType(eventType)
                                .eventVersion(EventVersions.V1)
                                .source(
                                        EventSources
                                                .BUSINESS_OPERATION_SERVICE
                                )
                                .occurredAt(LocalDateTime.now())
                                .correlationId(null)
                                .businessId(
                                        application.getBusinessId()
                                )
                                .performedBy(performedBy)
                                .performedByRole(performedByRole)
                                .description(description)
                                .build();

                publish(
                        event.getBusinessId(),
                        event
                );
            }

            default -> throw new IllegalArgumentException(
                    "Unsupported business operation event type: "
                            + eventType
            );
        }
    }
    
    public void publishCapabilityEnabledEvent(
            BusinessCapability capability,
            String performedBy,
            String performedByRole,
            String description) {

        CapabilityEnabledEvent event =
                CapabilityEnabledEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(
                                BusinessOperationEventType.CAPABILITY_ENABLED
                        )
                        .eventVersion(EventVersions.V1)
                        .source(
                                EventSources.BUSINESS_OPERATION_SERVICE
                        )
                        .occurredAt(LocalDateTime.now())
                        .correlationId(null)
                        .businessId(capability.getBusinessId())
                        .capabilityId(capability.getCapabilityId())
                        .capabilityType(
                                capability.getCapabilityType().name()
                        )
                        .performedBy(performedBy)
                        .performedByRole(performedByRole)
                        .description(description)
                        .build();

        publish(
                event.getBusinessId(),
                event
        );
    }

    public void publishCapabilityDisabledEvent(
            BusinessCapability capability,
            String performedBy,
            String performedByRole,
            String reason) {

        CapabilityDisabledEvent event =
                CapabilityDisabledEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(
                                BusinessOperationEventType.CAPABILITY_DISABLED
                        )
                        .eventVersion(EventVersions.V1)
                        .source(
                                EventSources.BUSINESS_OPERATION_SERVICE
                        )
                        .occurredAt(LocalDateTime.now())
                        .correlationId(null)
                        .businessId(capability.getBusinessId())
                        .capabilityId(capability.getCapabilityId())
                        .capabilityType(
                                capability.getCapabilityType().name()
                        )
                        .performedBy(performedBy)
                        .performedByRole(performedByRole)
                        .reason(reason)
                        .build();

        publish(
                event.getBusinessId(),
                event
        );
    }

    public void publishCapabilitySuspendedEvent(
            BusinessCapability capability,
            String performedBy,
            String performedByRole,
            String reason) {

        CapabilitySuspendedEvent event =
                CapabilitySuspendedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(
                                BusinessOperationEventType.CAPABILITY_SUSPENDED
                        )
                        .eventVersion(EventVersions.V1)
                        .source(
                                EventSources.BUSINESS_OPERATION_SERVICE
                        )
                        .occurredAt(LocalDateTime.now())
                        .correlationId(null)
                        .businessId(capability.getBusinessId())
                        .capabilityId(capability.getCapabilityId())
                        .capabilityType(
                                capability.getCapabilityType().name()
                        )
                        .performedBy(performedBy)
                        .performedByRole(performedByRole)
                        .reason(reason)
                        .build();

        publish(
                event.getBusinessId(),
                event
        );
    }

    public void publishDocumentUploadedEvent(
            BusinessDocument document,
            String userId,
            String description) {

        DocumentUploadedEvent event =
                DocumentUploadedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(
                                BusinessOperationEventType
                                        .DOCUMENT_UPLOADED
                        )
                        .eventVersion(EventVersions.V1)
                        .source(
                                EventSources
                                        .BUSINESS_OPERATION_SERVICE
                        )
                        .occurredAt(LocalDateTime.now())
                        .correlationId(null)
                        .businessId(
                                document.getBusinessId()
                        )
                        .applicationId(
                                document.getApplicationId()
                        )
                        .documentId(
                                document.getDocumentId()
                        )
                        .userId(userId)
                        .documentType(
                                document.getDocumentType() != null
                                        ? document.getDocumentType().name()
                                        : null
                        )
                        .documentName(
                                document.getDocumentName()
                        )
                        .description(description)
                        .build();

        publish(
                event.getBusinessId(),
                event
        );
    }

    public void publishDocumentApprovedEvent(
            BusinessDocument document,
            String performedBy,
            String performedByRole,
            String description) {

        DocumentApprovedEvent event =
                DocumentApprovedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(
                                BusinessOperationEventType
                                        .DOCUMENT_APPROVED
                        )
                        .eventVersion(EventVersions.V1)
                        .source(
                                EventSources
                                        .BUSINESS_OPERATION_SERVICE
                        )
                        .occurredAt(LocalDateTime.now())
                        .correlationId(null)
                        .businessId(
                                document.getBusinessId()
                        )
                        .applicationId(
                                document.getApplicationId()
                        )
                        .documentId(
                                document.getDocumentId()
                        )
                        .performedBy(performedBy)
                        .performedByRole(performedByRole)
                        .description(description)
                        .build();

        publish(
                event.getBusinessId(),
                event
        );
    }

    public void publishDocumentRejectedEvent(
            BusinessDocument document,
            String performedBy,
            String performedByRole,
            String reason) {

        DocumentRejectedEvent event =
                DocumentRejectedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(
                                BusinessOperationEventType
                                        .DOCUMENT_REJECTED
                        )
                        .eventVersion(EventVersions.V1)
                        .source(
                                EventSources
                                        .BUSINESS_OPERATION_SERVICE
                        )
                        .occurredAt(LocalDateTime.now())
                        .correlationId(null)
                        .businessId(
                                document.getBusinessId()
                        )
                        .applicationId(
                                document.getApplicationId()
                        )
                        .documentId(
                                document.getDocumentId()
                        )
                        .performedBy(performedBy)
                        .performedByRole(performedByRole)
                        .reason(reason)
                        .build();

        publish(
                event.getBusinessId(),
                event
        );
    }

    public void publishDocumentReuploadRequiredEvent(
            BusinessDocument document,
            String performedBy,
            String performedByRole,
            String reason) {

        DocumentReuploadRequiredEvent event =
                DocumentReuploadRequiredEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(
                                BusinessOperationEventType
                                        .DOCUMENT_REUPLOAD_REQUIRED
                        )
                        .eventVersion(EventVersions.V1)
                        .source(
                                EventSources
                                        .BUSINESS_OPERATION_SERVICE
                        )
                        .occurredAt(LocalDateTime.now())
                        .correlationId(null)
                        .businessId(
                                document.getBusinessId()
                        )
                        .applicationId(
                                document.getApplicationId()
                        )
                        .documentId(
                                document.getDocumentId()
                        )
                        .performedBy(performedBy)
                        .performedByRole(performedByRole)
                        .reason(reason)
                        .build();

        publish(
                event.getBusinessId(),
                event
        );
    }

    public void publishVerificationStartedEvent(
            BusinessVerification verification,
            String performedBy,
            String performedByRole,
            String description) {

        VerificationStartedEvent event =
                VerificationStartedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(
                                BusinessOperationEventType
                                        .VERIFICATION_STARTED
                        )
                        .eventVersion(EventVersions.V1)
                        .source(
                                EventSources
                                        .BUSINESS_OPERATION_SERVICE
                        )
                        .occurredAt(LocalDateTime.now())
                        .correlationId(null)
                        .businessId(
                                verification.getBusinessId()
                        )
                        .applicationId(
                                verification.getApplicationId()
                        )
                        .verificationId(
                                verification.getVerificationId()
                        )
                        .performedBy(performedBy)
                        .performedByRole(performedByRole)
                        .description(description)
                        .build();

        publish(
                event.getBusinessId(),
                event
        );
    }

    public void publishVerificationSubmittedEvent(
            BusinessVerification verification,
            String performedBy,
            String performedByRole,
            String description) {

        VerificationSubmittedEvent event =
                VerificationSubmittedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(
                                BusinessOperationEventType
                                        .VERIFICATION_SUBMITTED
                        )
                        .eventVersion(EventVersions.V1)
                        .source(
                                EventSources
                                        .BUSINESS_OPERATION_SERVICE
                        )
                        .occurredAt(LocalDateTime.now())
                        .correlationId(null)
                        .businessId(
                                verification.getBusinessId()
                        )
                        .applicationId(
                                verification.getApplicationId()
                        )
                        .verificationId(
                                verification.getVerificationId()
                        )
                        .performedBy(performedBy)
                        .performedByRole(performedByRole)
                        .description(description)
                        .build();

        publish(
                event.getBusinessId(),
                event
        );
    }

    public void publishVerificationApprovedEvent(
            BusinessVerification verification,
            String performedBy,
            String performedByRole,
            String description) {

        VerificationApprovedEvent event =
                VerificationApprovedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(
                                BusinessOperationEventType
                                        .VERIFICATION_APPROVED
                        )
                        .eventVersion(EventVersions.V1)
                        .source(
                                EventSources
                                        .BUSINESS_OPERATION_SERVICE
                        )
                        .occurredAt(LocalDateTime.now())
                        .correlationId(null)
                        .businessId(
                                verification.getBusinessId()
                        )
                        .applicationId(
                                verification.getApplicationId()
                        )
                        .verificationId(
                                verification.getVerificationId()
                        )
                        .performedBy(performedBy)
                        .performedByRole(performedByRole)
                        .description(description)
                        .build();

        publish(
                event.getBusinessId(),
                event
        );
    }

    public void publishVerificationRejectedEvent(
            BusinessVerification verification,
            String performedBy,
            String performedByRole,
            String reason) {

        VerificationRejectedEvent event =
                VerificationRejectedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(
                                BusinessOperationEventType
                                        .VERIFICATION_REJECTED
                        )
                        .eventVersion(EventVersions.V1)
                        .source(
                                EventSources
                                        .BUSINESS_OPERATION_SERVICE
                        )
                        .occurredAt(LocalDateTime.now())
                        .correlationId(null)
                        .businessId(
                                verification.getBusinessId()
                        )
                        .applicationId(
                                verification.getApplicationId()
                        )
                        .verificationId(
                                verification.getVerificationId()
                        )
                        .performedBy(performedBy)
                        .performedByRole(performedByRole)
                        .reason(reason)
                        .build();

        publish(
                event.getBusinessId(),
                event
        );
    }

    public void publishVerificationReuploadRequiredEvent(
            BusinessVerification verification,
            String performedBy,
            String performedByRole,
            String reason) {

        VerificationReuploadRequiredEvent event =
                VerificationReuploadRequiredEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(
                                BusinessOperationEventType
                                        .VERIFICATION_REUPLOAD_REQUIRED
                        )
                        .eventVersion(EventVersions.V1)
                        .source(
                                EventSources
                                        .BUSINESS_OPERATION_SERVICE
                        )
                        .occurredAt(LocalDateTime.now())
                        .correlationId(null)
                        .businessId(
                                verification.getBusinessId()
                        )
                        .applicationId(
                                verification.getApplicationId()
                        )
                        .verificationId(
                                verification.getVerificationId()
                        )
                        .performedBy(performedBy)
                        .performedByRole(performedByRole)
                        .reason(reason)
                        .build();

        publish(
                event.getBusinessId(),
                event
        );
    }

    private void publish(
            String businessId,
            Object event) {

        kafkaTemplate.send(
                KafkaTopics.BUSINESS_OPERATION_EVENTS,
                businessId,
                event
        );
    }
}
