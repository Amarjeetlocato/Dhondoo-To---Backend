package com.whoami.launch.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.locato.constants.topics.KafkaTopics;
import com.locato.dto.BusinessVerifiedEvent;
import com.locato.dto.ShopCreatedEvent;
import com.locato.dto.ShopStatusChangedEvent;
import com.locato.dto.ShopUpdatedEvent;
import com.locato.dto.UserCreatedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SuperUserEventConsumer {

    @KafkaListener(
            topics = {KafkaTopics.SHOP_EVENTS, KafkaTopics.BUSINESS_EVENTS, KafkaTopics.USER_EVENTS},
            groupId = "superuser-group"
    )
    public void consumeDomainEvents(ConsumerRecord<String, Object> record) {
        Object event = record.value();
        log.info("SuperUser Event Consumer -> Topic: {}, Payload: {}", record.topic(), event != null ? event.getClass().getSimpleName() : "null");

        if (event instanceof ShopCreatedEvent e) {
            log.info("SuperUser recorded new Shop/Business created: {} (ID: {}) for User: {}",
                    e.getShopName(), e.getShopId(), e.getUserId());

        } else if (event instanceof BusinessVerifiedEvent e) {
            log.info("SuperUser recorded Business Verification: {} -> Status: {}",
                    e.getBusinessName(), e.getVerificationStatus());

        } else if (event instanceof ShopStatusChangedEvent e) {
            log.info("SuperUser recorded Shop Status Change for ID: {} -> New Status: {}",
                    e.getShopId(), e.getShopStatus());

        } else if (event instanceof UserCreatedEvent e) {
            log.info("SuperUser recorded User Created event for Email: {}", e.getEmail());
        }
    }
}
