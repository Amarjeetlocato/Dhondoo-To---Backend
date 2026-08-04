package com.whoami.launch.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.locato.dto.ChatNotificationEvent;
import com.whoami.launch.dto.NotificationEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationProducer {

    private final KafkaTemplate<String, ChatNotificationEvent> kafkaTemplate;

    public void sendNotification(ChatNotificationEvent event) {
        kafkaTemplate.send("notification-topic", event);
    }
}