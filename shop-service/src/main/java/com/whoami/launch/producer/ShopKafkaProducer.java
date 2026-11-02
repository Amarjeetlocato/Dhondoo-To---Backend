package com.whoami.launch.producer;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.locato.constants.events.EventSources;
import com.locato.constants.events.EventVersions;
import com.locato.constants.events.business.BusinessCreatedEvent;
import com.locato.constants.events.business.BusinessDeletedEvent;
import com.locato.constants.events.business.BusinessEventType;
import com.locato.constants.events.business.BusinessStatusChangedEvent;
import com.locato.constants.events.business.BusinessUpdatedEvent;
import com.locato.constants.events.businessoperation.ApplicationCreationRequestedEvent;
import com.locato.constants.events.businessoperation.BusinessOperationEventType;
import com.locato.constants.topics.KafkaTopics;
import com.whoami.launch.entity.Business;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShopKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String topic, Object event) {
        CompletableFuture.runAsync(() -> {
            try {
                kafkaTemplate.send(topic, event);
            } catch (Exception e) {
                log.error("Failed to send event to topic {}: {}", topic, e.getMessage());
            }
        });
    }

    public void publishApplicationCreationRequested(Business business) {
        CompletableFuture.runAsync(() -> {
            try {
                ApplicationCreationRequestedEvent event =
                        ApplicationCreationRequestedEvent.builder()
                                .eventId(UUID.randomUUID())
                                .eventType(
                                        BusinessOperationEventType.APPLICATION_CREATION_REQUESTED)
                                .eventVersion(EventVersions.V1)
                                .source(EventSources.BUSINESS_SERVICE)
                                .occurredAt(LocalDateTime.now())
                                .businessId(business.getBusinessId())
                                .userId(business.getUserId())
                                .businessType(business.getBusinessType().name())
                                .businessName(business.getBusinessName())
                                .phone(business.getMobileNumber())
                                .address(business.getAddress())
                                .latitude(
                                        business.getLatitude() != null
                                                ? java.math.BigDecimal.valueOf(
                                                        business.getLatitude())
                                                : null)
                                .longitude(
                                        business.getLongitude() != null
                                                ? java.math.BigDecimal.valueOf(
                                                        business.getLongitude())
                                                : null)
                                .build();

                kafkaTemplate.send(
                        KafkaTopics.BUSINESS_OPERATION_EVENTS,
                        business.getBusinessId(),
                        event);
            } catch (Exception e) {
                log.error("Failed to publish ApplicationCreationRequestedEvent for businessId={}: {}", business.getBusinessId(), e.getMessage());
            }
        });
    }

    public void publishBusinessCreated(Business business) {
        CompletableFuture.runAsync(() -> {
            try {
                BusinessCreatedEvent event = BusinessCreatedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(BusinessEventType.BUSINESS_CREATED)
                        .eventVersion(EventVersions.V1)
                        .source(EventSources.BUSINESS_SERVICE)
                        .occurredAt(LocalDateTime.now())
                        .correlationId(UUID.randomUUID().toString())
                        .businessId(business.getBusinessId())
                        .userId(business.getUserId())
                        .businessName(business.getBusinessName())
                        .slug(business.getSlug())
                        .mobileNumber(business.getMobileNumber())
                        .logoUrl(null)
                        .bannerUrl(null)
                        .build();

                kafkaTemplate.send(
                        KafkaTopics.BUSINESS_EVENTS,
                        business.getBusinessId(),
                        event);

                if (business.getUserId() != null) {
                    com.locato.dto.NotificationEvent notif = com.locato.dto.NotificationEvent.builder()
                            .userId(business.getUserId())
                            .title("Shop Created 🎉")
                            .message("Congratulations! Your shop '" + business.getBusinessName() + "' was created successfully.")
                            .targetId(business.getBusinessId())
                            .targetType("SHOP")
                            .type("SYSTEM")
                            .sendPush(true)
                            .deepLink("/shop/account")
                            .build();
                    kafkaTemplate.send(KafkaTopics.NOTIFICATION_TOPIC, business.getUserId(), notif);

                    // Direct REST call fallback to Notification-Service
                    try {
                        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
                        java.util.Map<String, Object> req = new java.util.HashMap<>();
                        req.put("userId", business.getUserId());
                        req.put("title", "Shop Created 🎉");
                        req.put("message", "Congratulations! Your shop '" + business.getBusinessName() + "' was created successfully.");
                        req.put("targetId", business.getBusinessId());
                        req.put("targetType", "SHOP");
                        req.put("type", "SHOP");
                        req.put("sendPush", true);
                        req.put("deepLink", "/shop/account");

                        restTemplate.postForEntity("http://notification-service-notification-service.platform.svc.cluster.local:8087/api/notifications/internal-api/notifications", req, java.util.Map.class);
                        log.info("Sent Shop Created notification via REST fallback to Notification-Service for userId={}", business.getUserId());
                    } catch (Exception ne) {
                        log.warn("Direct notification REST fallback warning for userId={}: {}", business.getUserId(), ne.getMessage());
                    }
                }
            } catch (Exception e) {
                log.error("Failed to publish BusinessCreatedEvent for businessId={}: {}", business.getBusinessId(), e.getMessage());
            }
        });
    }

    public void publishBusinessUpdated(
            Business business,
            String changes) {
        CompletableFuture.runAsync(() -> {
            try {
                BusinessUpdatedEvent event = BusinessUpdatedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(BusinessEventType.BUSINESS_UPDATED.name())
                        .eventVersion(EventVersions.V1)
                        .source(EventSources.BUSINESS_SERVICE)
                        .occurredAt(LocalDateTime.now())
                        .correlationId(UUID.randomUUID().toString())
                        .businessId(business.getBusinessId())
                        .userId(business.getUserId())
                        .businessName(business.getBusinessName())
                        .mobileNumber(business.getMobileNumber())
                        .slug(business.getSlug())
                        .logoUrl(null)
                        .bannerUrl(null)
                        .changes(changes)
                        .build();

                kafkaTemplate.send(
                        KafkaTopics.BUSINESS_EVENTS,
                        business.getBusinessId(),
                        event);
            } catch (Exception e) {
                log.error("Failed to publish BusinessUpdatedEvent for businessId={}: {}", business.getBusinessId(), e.getMessage());
            }
        });
    }

    public void publishBusinessStatusChanged(
            Business business,
            String previousStatus) {
        CompletableFuture.runAsync(() -> {
            try {
                BusinessStatusChangedEvent event =
                        BusinessStatusChangedEvent.builder()
                                .eventId(UUID.randomUUID())
                                .eventType(
                                        BusinessEventType.BUSINESS_STATUS_CHANGED)
                                .eventVersion(EventVersions.V1)
                                .source(EventSources.BUSINESS_SERVICE)
                                .occurredAt(LocalDateTime.now())
                                .correlationId(UUID.randomUUID().toString())
                                .businessId(business.getBusinessId())
                                .userId(business.getUserId())
                                .businessName(business.getBusinessName())
                                .logoUrl(null)
                                .bannerUrl(null)
                                .previousStatus(previousStatus)
                                .businessStatus(
                                        business.getBusinessStatus() != null
                                                ? business.getBusinessStatus().name()
                                                : null)
                                .build();

                kafkaTemplate.send(
                        KafkaTopics.BUSINESS_EVENTS,
                        business.getBusinessId(),
                        event);
            } catch (Exception e) {
                log.error("Failed to publish BusinessStatusChangedEvent for businessId={}: {}", business.getBusinessId(), e.getMessage());
            }
        });
    }

    public void publishBusinessDeleted(Business business) {
        CompletableFuture.runAsync(() -> {
            try {
                BusinessDeletedEvent event = BusinessDeletedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(BusinessEventType.BUSINESS_DELETED.name())
                        .eventVersion(EventVersions.V1)
                        .source(EventSources.BUSINESS_SERVICE)
                        .occurredAt(LocalDateTime.now())
                        .correlationId(UUID.randomUUID().toString())
                        .businessId(business.getBusinessId())
                        .userId(business.getUserId())
                        .businessName(business.getBusinessName())
                        .logoUrl(null)
                        .bannerUrl(null)
                        .build();

                kafkaTemplate.send(
                        KafkaTopics.BUSINESS_EVENTS,
                        business.getBusinessId(),
                        event);
            } catch (Exception e) {
                log.error("Failed to publish BusinessDeletedEvent for businessId={}: {}", business.getBusinessId(), e.getMessage());
            }
        });
    }
}
