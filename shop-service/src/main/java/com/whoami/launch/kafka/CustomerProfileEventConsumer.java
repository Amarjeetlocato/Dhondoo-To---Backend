package com.whoami.launch.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.locato.constants.events.customer.CustomerProfileCreatedEvent;
import com.whoami.launch.entity.CustomerProfile;
import com.whoami.launch.repository.CustomerProfileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerProfileEventConsumer {

    private final CustomerProfileRepository customerProfileRepository;

    @KafkaListener(
            topics = "customer-events",
            groupId = "shop-customer-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    @Transactional
    public void consumeCustomerProfileCreated(
            CustomerProfileCreatedEvent event) {

        log.info(
                "Received CUSTOMER_PROFILE_CREATED event. userId={}, fullName={}, email={}",
                event.getUserId(),
                event.getFullName(),
                event.getEmail()
        );

        if (event.getUserId() == null || event.getUserId().isBlank()) {
            log.warn("Ignoring customer event because userId is missing");
            return;
        }

        if (customerProfileRepository.existsByUserId(event.getUserId())) {
            log.info(
                    "CustomerProfile already exists for userId={}. Skipping.",
                    event.getUserId()
            );
            return;
        }

        CustomerProfile profile = new CustomerProfile();

        profile.setUserId(event.getUserId());
        profile.setFullName(event.getFullName());
        profile.setEmail(event.getEmail());

        CustomerProfile saved =
                customerProfileRepository.save(profile);

        log.info(
                "CustomerProfile created successfully. customerId={}, userId={}",
                saved.getCustomerId(),
                saved.getUserId()
        );
    }
}