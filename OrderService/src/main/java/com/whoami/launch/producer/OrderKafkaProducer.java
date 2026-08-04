package com.whoami.launch.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String topic, Object event) {
        kafkaTemplate.send(topic, event);
    }
}