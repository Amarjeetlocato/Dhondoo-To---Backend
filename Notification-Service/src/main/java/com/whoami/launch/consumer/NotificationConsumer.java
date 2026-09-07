package com.whoami.launch.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.locato.dto.ChatNotificationEvent;
import com.whoami.launch.dto.NotificationRequest;
import com.whoami.launch.enums.NotificationType;
import com.whoami.launch.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "notification-topic",
            groupId = "notification-group"
    )
    public void consume(ChatNotificationEvent event) {

        NotificationRequest request = NotificationRequest.builder()
                .userId(event.getReceiverId())
                .title("New Chat Message")
                .message(event.getMessagePreview())
                .imageUrl(event.getSenderImage())
                .targetId(event.getConversationId())
                .targetType("CHAT")
                .type(NotificationType.CHAT)
                .sendPush(true)
                .metadataJson(null)
                .actionsJson(null)
                .deepLink(null)
                .build();

        notificationService.createNotification(request);
    }
}
