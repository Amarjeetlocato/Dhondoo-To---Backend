package com.whoami.launch.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.locato.constants.events.business.BusinessCreatedEvent;
import com.locato.constants.events.business.BusinessDeletedEvent;
import com.locato.constants.events.business.BusinessStatusChangedEvent;
import com.locato.constants.events.business.BusinessUpdatedEvent;
import com.locato.constants.events.chat.ChatNotificationEvent;
import com.locato.constants.topics.KafkaTopics;
import com.whoami.launch.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BusinessEventConsumer {

    private final 	NotificationService notificationService;

    @KafkaListener(
            topics = KafkaTopics.BUSINESS_EVENTS,
            groupId = "notification-group"
    )
    public void consume(ConsumerRecord<String, Object> record) {

        Object event = record.value();

        System.out.println("========== MESSAGE RECEIVED ==========");
        System.out.println("Payload = " + event.getClass().getSimpleName());

        if (event instanceof BusinessCreatedEvent e) {
            notificationService.handleBusinessCreated(e);

        } else if (event instanceof BusinessUpdatedEvent e) {
            notificationService.handleBusinessUpdated(e);

        } else if (event instanceof BusinessDeletedEvent  e) {
            notificationService.handleBusinessDeleted(e);

        } else if (event instanceof BusinessStatusChangedEvent e) {
            notificationService.handleBusinessStatusChanged(e);

        } else {
            System.out.println("Unknown event: " + event.getClass());
        }
    }
    
    @KafkaListener(
    	    topics = KafkaTopics.CHAT_EVENTS,
    	    groupId = "notification-group"
    	)
    	public void consumeChatNotification(
    	        ChatNotificationEvent event) {

    	}
}
