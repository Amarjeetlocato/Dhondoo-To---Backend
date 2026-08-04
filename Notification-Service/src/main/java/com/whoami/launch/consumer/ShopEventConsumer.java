package com.whoami.launch.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.whoami.launch.dto.KafkaTopics;
import com.locato.dto.ChatNotificationEvent;
import com.locato.dto.ShopCreatedEvent;
import com.locato.dto.ShopDeletedEvent;
import com.locato.dto.ShopStatusChangedEvent;
import com.locato.dto.ShopUpdatedEvent;
import com.whoami.launch.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ShopEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = KafkaTopics.SHOP_EVENTS,
            groupId = "notification-group"
    )
    public void consume(ConsumerRecord<String, Object> record) {

        Object event = record.value();

        System.out.println("========== MESSAGE RECEIVED ==========");
        System.out.println("Payload = " + event.getClass().getSimpleName());

        if (event instanceof ShopCreatedEvent e) {
            notificationService.handleShopCreated(e);

        } else if (event instanceof ShopUpdatedEvent e) {
            notificationService.handleShopUpdated(e);

        } else if (event instanceof ShopDeletedEvent e) {
            notificationService.handleShopDeleted(e);

        } else if (event instanceof ShopStatusChangedEvent e) {
            notificationService.handleShopStatusChanged(e);

        } else {
            System.out.println("Unknown event: " + event.getClass());
        }
    }
    
    @KafkaListener(
    	    topics = KafkaTopics.CHAT_NOTIFICATION,
    	    groupId = "notification-group"
    	)
    	public void consumeChatNotification(
    	        ChatNotificationEvent event) {

    	}
}
