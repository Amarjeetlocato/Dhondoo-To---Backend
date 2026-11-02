package com.whoami.launch.producer;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.locato.constants.events.EventSources;
import com.locato.constants.events.EventVersions;
import com.locato.constants.events.reel.ReelCreatedEvent;
import com.locato.constants.events.reel.ReelDeletedEvent;
import com.locato.constants.events.reel.ReelEventType;
import com.locato.constants.events.reel.ReelUpdatedEvent;
import com.locato.constants.topics.KafkaTopics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReelEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishReelCreated(ReelCreatedEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                if (event.getEventId() == null) {
                    event.setEventId(UUID.randomUUID());
                }

                event.setEventType(ReelEventType.REEL_CREATED);
                event.setEventVersion(EventVersions.V1);
                event.setSource(EventSources.BUSINESS_SERVICE);

                if (event.getOccurredAt() == null) {
                    event.setOccurredAt(LocalDateTime.now());
                }

                kafkaTemplate.send(
                        KafkaTopics.REEL_EVENTS,
                        event.getReelId(),
                        event
                );

                if (event.getUserId() != null) {
                    try {
                        org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
                        java.util.Map<String, Object> req = new java.util.HashMap<>();
                        req.put("userId", event.getUserId());
                        req.put("title", "New Reel Posted 🎬");
                        req.put("message", "New promotional video reel posted successfully for your store.");
                        req.put("targetId", event.getReelId());
                        req.put("targetType", "REEL");
                        req.put("type", "REEL");
                        req.put("sendPush", true);
                        req.put("deepLink", "/shop/account");

                        restTemplate.postForEntity("http://notification-service.platform.svc.cluster.local:8087/api/notifications/internal-api/notifications", req, java.util.Map.class);
                        log.info("Sent Reel Created notification via REST for userId={}", event.getUserId());
                    } catch (Exception ne) {
                        log.warn("Could not send direct reel notification via REST: {}", ne.getMessage());
                    }
                }
            } catch (Exception e) {
                log.error("Error publishing ReelCreatedEvent for reelId={}: {}", event.getReelId(), e.getMessage());
            }
        });
    }

    public void publishReelUpdated(ReelUpdatedEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                if (event.getEventId() == null) {
                    event.setEventId(UUID.randomUUID());
                }

                event.setEventType(ReelEventType.REEL_UPDATED);
                event.setEventVersion(EventVersions.V1);
                event.setSource(EventSources.BUSINESS_SERVICE);

                if (event.getOccurredAt() == null) {
                    event.setOccurredAt(LocalDateTime.now());
                }

                kafkaTemplate.send(
                        KafkaTopics.REEL_EVENTS,
                        event.getReelId(),
                        event
                );
            } catch (Exception e) {
                log.error("Error publishing ReelUpdatedEvent for reelId={}: {}", event.getReelId(), e.getMessage());
            }
        });
    }

    public void publishReelDeleted(ReelDeletedEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                if (event.getEventId() == null) {
                    event.setEventId(UUID.randomUUID());
                }

                event.setEventType(ReelEventType.REEL_DELETED.name());
                event.setEventVersion(EventVersions.V1);
                event.setSource(EventSources.BUSINESS_SERVICE);

                if (event.getOccurredAt() == null) {
                    event.setOccurredAt(LocalDateTime.now());
                }

                kafkaTemplate.send(
                        KafkaTopics.REEL_EVENTS,
                        event.getReelId(),
                        event
                );
            } catch (Exception e) {
                log.error("Error publishing ReelDeletedEvent for reelId={}: {}", event.getReelId(), e.getMessage());
            }
        });
    }
}
