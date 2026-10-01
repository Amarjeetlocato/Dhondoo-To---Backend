package com.whoami.launch.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.locato.constants.events.reel.ReelCreatedEvent;
import com.locato.constants.events.reel.ReelDeletedEvent;
import com.locato.constants.events.reel.ReelUpdatedEvent;
import com.locato.constants.topics.KafkaTopics;
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