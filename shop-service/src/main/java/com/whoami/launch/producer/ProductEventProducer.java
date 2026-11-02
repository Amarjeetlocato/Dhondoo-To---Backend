package com.whoami.launch.producer;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.locato.constants.events.EventSources;
import com.locato.constants.events.EventVersions;
import com.locato.constants.events.product.ProductCreatedEvent;
import com.locato.constants.events.product.ProductDeletedEvent;
import com.locato.constants.events.product.ProductEventType;
import com.locato.constants.events.product.ProductUpdatedEvent;
import com.locato.constants.topics.KafkaTopics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishProductCreated(ProductCreatedEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                event.setEventId(event.getEventId() != null
                        ? event.getEventId() : UUID.randomUUID());
                event.setEventType(ProductEventType.PRODUCT_CREATED);
                event.setEventVersion(EventVersions.V1);
                event.setSource(EventSources.BUSINESS_SERVICE);

                if (event.getOccurredAt() == null) {
                    event.setOccurredAt(LocalDateTime.now());
                }

                kafkaTemplate.send(
                        KafkaTopics.PRODUCT_EVENTS,
                        event.getProductId(),
                        event
                );

                if (event.getUserId() != null) {
                    try {
                        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
                        java.util.Map<String, Object> req = new java.util.HashMap<>();
                        req.put("userId", event.getUserId());
                        req.put("title", "New Product Added 📦");
                        req.put("message", "Product '" + event.getProductName() + "' was added successfully to your store.");
                        req.put("targetId", event.getProductId());
                        req.put("targetType", "PRODUCT");
                        req.put("type", "PRODUCT");
                        req.put("sendPush", true);
                        req.put("deepLink", "/shop/account");

                        restTemplate.postForEntity("http://notification-service-notification-service.platform.svc.cluster.local:8087/api/notifications/internal-api/notifications", req, java.util.Map.class);
                        log.info("Sent Product Created notification via REST for userId={}", event.getUserId());
                    } catch (Exception ne) {
                        log.warn("Could not send direct product notification via REST: {}", ne.getMessage());
                    }
                }
            } catch (Exception e) {
                log.error("Error publishing ProductCreatedEvent for productId={}: {}", event.getProductId(), e.getMessage());
            }
        });
    }

    public void publishProductUpdated(ProductUpdatedEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                event.setEventId(event.getEventId() != null
                        ? event.getEventId() : UUID.randomUUID());
                event.setEventType(ProductEventType.PRODUCT_UPDATED);
                event.setEventVersion(EventVersions.V1);
                event.setSource(EventSources.BUSINESS_SERVICE);

                if (event.getOccurredAt() == null) {
                    event.setOccurredAt(LocalDateTime.now());
                }

                kafkaTemplate.send(
                        KafkaTopics.PRODUCT_EVENTS,
                        event.getProductId(),
                        event
                );
            } catch (Exception e) {
                log.error("Error publishing ProductUpdatedEvent for productId={}: {}", event.getProductId(), e.getMessage());
            }
        });
    }

    public void publishProductDeleted(ProductDeletedEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                event.setEventId(event.getEventId() != null
                        ? event.getEventId() : UUID.randomUUID());
                event.setEventType(ProductEventType.PRODUCT_DELETED);
                event.setEventVersion(EventVersions.V1);
                event.setSource(EventSources.BUSINESS_SERVICE);

                if (event.getOccurredAt() == null) {
                    event.setOccurredAt(LocalDateTime.now());
                }

                kafkaTemplate.send(
                        KafkaTopics.PRODUCT_EVENTS,
                        event.getProductId(),
                        event
                );
            } catch (Exception e) {
                log.error("Error publishing ProductDeletedEvent for productId={}: {}", event.getProductId(), e.getMessage());
            }
        });
    }
}
