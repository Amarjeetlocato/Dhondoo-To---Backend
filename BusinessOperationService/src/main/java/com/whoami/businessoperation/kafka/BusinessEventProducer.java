package com.whoami.businessoperation.kafka;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.locato.constants.events.businessoperation.ApplicationCreatedEvent;
import com.locato.constants.events.businessoperation.ApplicationSubmittedEvent;
import com.locato.constants.events.businessoperation.BusinessOperationEventType;
import com.locato.constants.topics.KafkaTopics;
import com.whoami.businessoperation.domain.entity.BusinessApplication;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BusinessEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishApplicationCreatedEvent(
            ApplicationCreatedEvent event) {

        kafkaTemplate.send(
                KafkaTopics.BUSINESS_OPERATION_EVENTS,
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

        if (eventType == BusinessOperationEventType.APPLICATION_CREATED) {

            ApplicationCreatedEvent event =
                    ApplicationCreatedEvent.builder()
                            .eventId(UUID.randomUUID())
                            .eventType(eventType)
                            .eventVersion(1)
                            .source("business-operation-service")
                            .occurredAt(LocalDateTime.now())
                            .correlationId(null)
                            .businessId(application.getBusinessId())
                            .applicationId(application.getApplicationId())
                            .userId(application.getOwnerUserId())
                            .description(description)
                            .build();

            publishApplicationCreatedEvent(event);

            return;
        }

        if (eventType == BusinessOperationEventType.APPLICATION_SUBMITTED) {

            ApplicationSubmittedEvent event =
                    ApplicationSubmittedEvent.builder()
                            .eventId(UUID.randomUUID())
                            .eventType(eventType)
                            .eventVersion(1)
                            .source("business-operation-service")
                            .occurredAt(LocalDateTime.now())
                            .correlationId(null)
                            .businessId(application.getBusinessId())
                            .applicationId(application.getApplicationId())
                            .userId(performedBy)
                            .description(description)
                            .build();

            kafkaTemplate.send(
                    KafkaTopics.BUSINESS_OPERATION_EVENTS,
                    event.getBusinessId(),
                    event
            );

            return;
        }

        throw new IllegalArgumentException(
                "Unsupported business operation event type: "
                        + eventType
        );
    }
}