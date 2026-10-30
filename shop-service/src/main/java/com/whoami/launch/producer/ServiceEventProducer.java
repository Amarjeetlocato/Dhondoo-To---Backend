package com.whoami.launch.producer;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.locato.constants.events.EventSources;
import com.locato.constants.events.EventVersions;
import com.locato.constants.events.service.ServiceCreatedEvent;
import com.locato.constants.events.service.ServiceDeletedEvent;
import com.locato.constants.events.service.ServiceEventType;
import com.locato.constants.events.service.ServiceUpdatedEvent;
import com.locato.constants.topics.KafkaTopics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishServiceCreated(ServiceCreatedEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                event.setEventId(event.getEventId() != null
                        ? event.getEventId() : UUID.randomUUID());
                event.setEventType(ServiceEventType.SERVICE_CREATED);
                event.setEventVersion(EventVersions.V1);
                event.setSource(EventSources.BUSINESS_SERVICE);
                event.setOccurredAt(LocalDateTime.now());

                kafkaTemplate.send(
                        KafkaTopics.SERVICE_EVENTS,
                        event.getServiceId(),
                        event
                );

                if (event.getUserId() != null) {
                    try {
                        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
                        java.util.Map<String, Object> req = new java.util.HashMap<>();
                        req.put("userId", event.getUserId());
                        req.put("title", "New Service Added 🛠️");
                        req.put("message", "Service '" + event.getServiceName() + "' was added successfully to your store.");
                        req.put("targetId", event.getServiceId());
                        req.put("targetType", "SERVICE");
                        req.put("type", "SERVICE");
                        req.put("sendPush", true);
                        req.put("deepLink", "/shop/account");

                        restTemplate.postForEntity("http://localhost:8087/api/notifications/internal-api/notifications", req, java.util.Map.class);
                        log.info("Sent Service Created notification via REST for userId={}", event.getUserId());
                    } catch (Exception ne) {
                        log.warn("Could not send direct service notification via REST: {}", ne.getMessage());
                    }
                }
            } catch (Exception e) {
                log.error("Error publishing ServiceCreatedEvent for serviceId={}: {}", event.getServiceId(), e.getMessage());
            }
        });
    }

    public void publishServiceUpdated(ServiceUpdatedEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                event.setEventId(event.getEventId() != null
                        ? event.getEventId() : UUID.randomUUID());
                event.setEventType(ServiceEventType.SERVICE_UPDATED);
                event.setEventVersion(EventVersions.V1);
                event.setSource(EventSources.BUSINESS_SERVICE);
                event.setOccurredAt(LocalDateTime.now());

                kafkaTemplate.send(
                        KafkaTopics.SERVICE_EVENTS,
                        event.getServiceId(),
                        event
                );
            } catch (Exception e) {
                log.error("Error publishing ServiceUpdatedEvent for serviceId={}: {}", event.getServiceId(), e.getMessage());
            }
        });
    }

    public void publishServiceDeleted(ServiceDeletedEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                event.setEventId(event.getEventId() != null
                        ? event.getEventId() : UUID.randomUUID());
                event.setEventType(ServiceEventType.SERVICE_DELETED);
                event.setEventVersion(EventVersions.V1);
                event.setSource(EventSources.BUSINESS_SERVICE);
                event.setOccurredAt(LocalDateTime.now());

                kafkaTemplate.send(
                        KafkaTopics.SERVICE_EVENTS,
                        event.getServiceId(),
                        event
                );
            } catch (Exception e) {
                log.error("Error publishing ServiceDeletedEvent for serviceId={}: {}", event.getServiceId(), e.getMessage());
            }
        });
    }
}
