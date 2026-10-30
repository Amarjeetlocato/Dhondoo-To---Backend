package com.whoami.launch.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.locato.constants.events.chat.ChatNotificationEvent;
import com.locato.dto.NotificationEvent;
import com.whoami.launch.dto.NotificationRequest;
import com.whoami.launch.enums.NotificationType;
import com.whoami.launch.service.NotificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = {"notification-topic", "notification-events"},
            groupId = "notification-group"
    )
    public void consume(ConsumerRecord<String, Object> record) {
        Object event = record.value();
        log.info("NotificationConsumer received payload: {}", event != null ? event.getClass().getSimpleName() : "null");

        if (event instanceof NotificationEvent ne) {
            NotificationType type = NotificationType.SYSTEM;
            if (ne.getType() != null) {
                try {
                    type = NotificationType.valueOf(ne.getType().toUpperCase());
                } catch (Exception ignored) {
                }
            }

            NotificationRequest request = NotificationRequest.builder()
                    .userId(ne.getUserId())
                    .title(ne.getTitle())
                    .message(ne.getMessage())
                    .imageUrl(ne.getImageUrl())
                    .targetId(ne.getTargetId())
                    .targetType(ne.getTargetType())
                    .type(type)
                    .sendPush(ne.getSendPush() != null ? ne.getSendPush() : true)
                    .metadataJson(ne.getMetadataJson())
                    .actionsJson(ne.getActionsJson())
                    .deepLink(ne.getDeepLink())
                    .build();

            notificationService.createNotification(request);

        } else if (event instanceof ChatNotificationEvent ce) {
            NotificationRequest request = NotificationRequest.builder()
                    .userId(ce.getReceiverId())
                    .title("New Chat Message 💬")
                    .message(ce.getMessagePreview())
                    .imageUrl(ce.getSenderImage())
                    .targetId(ce.getConversationId())
                    .targetType("CHAT")
                    .type(NotificationType.CHAT)
                    .sendPush(true)
                    .deepLink("/chat/" + ce.getConversationId())
                    .build();

            notificationService.createNotification(request);
        }
    }
}
