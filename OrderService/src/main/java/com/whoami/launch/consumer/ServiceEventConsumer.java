package com.whoami.launch.consumer;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.locato.dto.ServiceCreatedEvent;
import com.locato.dto.ServiceDeletedEvent;
import com.locato.dto.ServiceUpdatedEvent;
import com.whoami.launch.order.orders.service.OrderSyncService;
import com.locato.topics.KafkaTopics;

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