package com.whoami.launch.dto;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CustomerEventProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void publishCustomerProfileCreated(CustomerProfileCreatedEvent event) {

        kafkaTemplate.send(
                "customer-profile-created",
                event
        );
    }
}