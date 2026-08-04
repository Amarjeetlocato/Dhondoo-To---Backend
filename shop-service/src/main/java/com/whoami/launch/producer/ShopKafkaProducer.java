package com.whoami.launch.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ShopKafkaProducer {

    private final KafkaTemplate<String,Object> kafkaTemplate;

    public ShopKafkaProducer(
            KafkaTemplate<String,Object> kafkaTemplate
    ) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(
            String topic,
            Object event
    ) {

        kafkaTemplate.send(
                topic,
                event
        );

    }

}