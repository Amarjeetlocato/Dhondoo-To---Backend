package com.whoami.launch.service.impl;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.locato.constants.events.chat.ChatNotificationEvent;
import com.whoami.launch.service.NotificationProducer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationProducerImpl
        implements NotificationProducer {

    private static final String NOTIFICATION_TOPIC =
            "notification-topic";

    private final KafkaTemplate<
            String,
            ChatNotificationEvent> kafkaTemplate;

    @Override
    public void sendNotification(
            ChatNotificationEvent event) {

        kafkaTemplate.send(
                NOTIFICATION_TOPIC,
                event);
    }
}