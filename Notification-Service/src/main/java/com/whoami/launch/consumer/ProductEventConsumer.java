package com.whoami.launch.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.locato.topics.KafkaTopics;
import com.locato.dto.ProductCreatedEvent;
import com.locato.dto.ProductUpdatedEvent;
import com.locato.dto.ProductDeletedEvent;
import com.whoami.launch.service.NotificationService;

import org.apache.kafka.clients.consumer.ConsumerRecord;


import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductEventConsumer {

    private final NotificationService notificationService;


    @KafkaListener(
            topics = KafkaTopics.PRODUCT_EVENTS,
            groupId = "notification-group"
    )
    public void consume(
            ConsumerRecord<String, Object> record
    ) {

        System.out.println("========== PRODUCT KAFKA RECEIVED ==========");

        Object event = record.value();

        System.out.println("Value Class = "
                + event.getClass().getName());

        System.out.println("Value = " + event);

        if (event instanceof ProductCreatedEvent e) {

            System.out.println("PRODUCT CREATED DETECTED");

            notificationService.handleProductCreated(e);
        }

        else if (event instanceof ProductUpdatedEvent e) {

            System.out.println("PRODUCT UPDATED DETECTED");

            notificationService.handleProductUpdated(e);
        }

        else if (event instanceof ProductDeletedEvent e) {

            System.out.println("PRODUCT DELETED DETECTED");

            notificationService.handleProductDeleted(e);
        }
    }

}