package com.whoami.launch.kafka;

import com.locato.constants.events.businessoperation.CapabilityDisabledEvent;
import com.locato.constants.events.businessoperation.CapabilityEnabledEvent;
import com.locato.constants.events.businessoperation.CapabilitySuspendedEvent;
import com.locato.constants.events.businessoperation.VerificationApprovedEvent;
import com.locato.constants.events.businessoperation.VerificationRejectedEvent;
import com.locato.constants.events.businessoperation.VerificationReuploadRequiredEvent;
import com.whoami.launch.entity.BusinessAccessState;
import com.whoami.launch.repository.BusinessAccessStateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BusinessOperationEventConsumer {

    private final BusinessAccessStateRepository accessStateRepository;

    @KafkaListener(
            topics = "business-operation-events",
            groupId = "shop-business-operation-group",
            containerFactory = "businessOperationKafkaListenerContainerFactory"
    )
    @Transactional
    public void consume(ConsumerRecord<String, Object> record) {

        Object event = record.value();

        log.info(
                "BusinessOperation event class = {}",
                event.getClass().getName()
        );

        if (event instanceof VerificationApprovedEvent verificationEvent) {

            updateVerification(
                    verificationEvent.getBusinessId(),
                    true
            );

            log.info(
                    "Business verification approved. businessId={}",
                    verificationEvent.getBusinessId()
            );

        } else if (event instanceof VerificationRejectedEvent verificationEvent) {

            updateVerification(
                    verificationEvent.getBusinessId(),
                    false
            );

            log.info(
                    "Business verification rejected. businessId={}",
                    verificationEvent.getBusinessId()
            );

        } else if (event instanceof VerificationReuploadRequiredEvent verificationEvent) {

            updateVerification(
                    verificationEvent.getBusinessId(),
                    false
            );

            log.info(
                    "Business verification reupload required. businessId={}",
                    verificationEvent.getBusinessId()
            );

        } else if (event instanceof CapabilityEnabledEvent capabilityEvent) {

            if ("SUBSCRIPTION".equalsIgnoreCase(
                    capabilityEvent.getCapabilityType())) {

                updateSubscription(
                        capabilityEvent.getBusinessId(),
                        true
                );

                log.info(
                        "Business subscription enabled. businessId={}",
                        capabilityEvent.getBusinessId()
                );
            }

        } else if (event instanceof CapabilityDisabledEvent capabilityEvent) {

            if ("SUBSCRIPTION".equalsIgnoreCase(
                    capabilityEvent.getCapabilityType())) {

                updateSubscription(
                        capabilityEvent.getBusinessId(),
                        false
                );

                log.info(
                        "Business subscription disabled. businessId={}",
                        capabilityEvent.getBusinessId()
                );
            }

        } else if (event instanceof CapabilitySuspendedEvent capabilityEvent) {

            if ("SUBSCRIPTION".equalsIgnoreCase(
                    capabilityEvent.getCapabilityType())) {

                updateSubscription(
                        capabilityEvent.getBusinessId(),
                        false
                );

                log.info(
                        "Business subscription suspended. businessId={}",
                        capabilityEvent.getBusinessId()
                );
            }
        }
    }

    private void updateVerification(
            String businessId,
            boolean verified) {

        BusinessAccessState state =
                getOrCreate(businessId);

        state.setVerified(verified);

        accessStateRepository.save(state);
    }

    private void updateSubscription(
            String businessId,
            boolean active) {

        BusinessAccessState state =
                getOrCreate(businessId);

        state.setSubscriptionActive(active);

        accessStateRepository.save(state);
    }

    private BusinessAccessState getOrCreate(
            String businessId) {

        return accessStateRepository
                .findByBusinessId(businessId)
                .orElseGet(() -> {

                    BusinessAccessState state =
                            new BusinessAccessState();

                    state.setBusinessId(businessId);

                    return state;
                });
    }
}