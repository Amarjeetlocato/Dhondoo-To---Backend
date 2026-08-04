package com.whoami.launch.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.whoami.launch.dto.UserCreatedEvent;
import com.whoami.launch.service.CustomerProfileService;

@Component
public class UserCreatedConsumer {

    @Autowired
    private CustomerProfileService customerProfileService;

    @KafkaListener(
            topics = "user-created",
            groupId = "customer-service"
    )
    public void consume(UserCreatedEvent event) {

        customerProfileService.createCustomerProfileFromUser(event);

    }
}