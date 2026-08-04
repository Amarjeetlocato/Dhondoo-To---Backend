package com.whoami.launch.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.whoami.launch.dto.KafkaTopics;
import com.locato.dto.ReelCreatedEvent;
import com.locato.dto.ReelDeletedEvent;
import com.locato.dto.ReelUpdatedEvent;
import com.whoami.launch.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReelEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = KafkaTopics.REEL_EVENTS,
            groupId = "notification-group"
    )
    public void consume(Object event) {

        if (event instanceof ReelCreatedEvent e) {
            notificationService.handleReelCreated(e);
        }

        else if (event instanceof ReelUpdatedEvent e) {
            notificationService.handleReelUpdated(e);
        }

        else if (event instanceof ReelDeletedEvent e) {
            notificationService.handleReelDeleted(e);
        }
    }

}