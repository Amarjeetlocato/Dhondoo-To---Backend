package com.whoami.launch.consumer;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.locato.constants.events.service.ServiceCreatedEvent;
import com.locato.constants.events.service.ServiceDeletedEvent;
import com.locato.constants.events.service.ServiceUpdatedEvent;
import com.locato.constants.topics.KafkaTopics;
import com.whoami.launch.order.orders.service.OrderSyncService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ServiceEventConsumer {

    private final OrderSyncService orderSyncService;

    @KafkaListener(
            topics = KafkaTopics.SERVICE_EVENTS,
            groupId = "order-group"
    )
    public void consume(Object event) {

        if (event instanceof ServiceCreatedEvent e) {
            orderSyncService.handleServiceCreated(e);
        }

        else if (event instanceof ServiceUpdatedEvent e) {
            orderSyncService.handleServiceUpdated(e);
        }

        else if (event instanceof ServiceDeletedEvent e) {
            orderSyncService.handleServiceDeleted(e);
        }
    }
}