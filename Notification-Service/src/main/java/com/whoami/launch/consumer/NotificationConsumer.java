package com.whoami.launch.consumer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.whoami.launch.dto.NotificationEvent;
import com.whoami.launch.dto.NotificationRequest;
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
    public void consume(NotificationEvent event) {

        NotificationRequest request = NotificationRequest.builder()
                .userId(event.getUserId())
                .title(event.getTitle())
                .message(event.getMessage())
                .imageUrl(event.getImageUrl())
                .targetId(event.getTargetId())
                .targetType(event.getTargetType())
                .type(event.getType())
                .sendPush(event.getSendPush())
                .metadataJson(event.getMetadataJson())
                .actionsJson(event.getActionsJson())
                .deepLink(event.getDeepLink())
                .build();

        
        
        notificationService.createNotification(request);
    }
}
