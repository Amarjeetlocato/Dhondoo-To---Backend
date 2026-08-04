package com.whoami.launch.consumer;



import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.locato.dto.ProductCreatedEvent;
import com.locato.dto.ProductDeletedEvent;
import com.locato.dto.ProductUpdatedEvent;
import com.whoami.launch.order.orders.service.OrderSyncService;
import com.locato.topics.KafkaTopics;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProductEventConsumer {

	 private final OrderSyncService orderSyncService;
    @KafkaListener(
            topics = KafkaTopics.PRODUCT_EVENTS,
            groupId = "order-group"
    )
    public void consume(Object event) {

        if (event instanceof ProductCreatedEvent e) {
            orderSyncService.handleProductCreated(e);
        }

        else if (event instanceof ProductUpdatedEvent e) {
            orderSyncService.handleProductUpdated(e);
        }

        else if (event instanceof ProductDeletedEvent e) {
            orderSyncService.handleProductDeleted(e);
        }
    }
}