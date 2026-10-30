package com.whoami.launch.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.locato.constants.events.businessoperation.BusinessOperationEventType;
import com.locato.constants.events.businessoperation.CapabilityDisabledEvent;
import com.locato.constants.events.businessoperation.CapabilityEnabledEvent;
import com.locato.constants.events.businessoperation.CapabilitySuspendedEvent;
import com.locato.constants.events.businessoperation.VerificationApprovedEvent;
import com.locato.constants.events.businessoperation.VerificationRejectedEvent;
import com.locato.constants.events.businessoperation.VerificationReuploadRequiredEvent;
import com.locato.constants.topics.KafkaTopics;
import com.locato.dto.BusinessVerifiedEvent;
import com.locato.dto.NotificationEvent;
import com.locato.dto.PaymentEvent;
import com.locato.dto.ShopStatusChangedEvent;
import com.locato.enums.ShopStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisherService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publishes notification event to Notification-Service via Kafka.
     */
    public void sendNotification(
            String userId,
            String title,
            String message,
            String targetId,
            String targetType,
            String notificationType,
            String deepLink
    ) {
        try {
            NotificationEvent event = NotificationEvent.builder()
                    .userId(userId)
                    .title(title)
                    .message(message)
                    .targetId(targetId)
                    .targetType(targetType)
                    .type(notificationType)
                    .sendPush(true)
                    .deepLink(deepLink)
                    .build();

            kafkaTemplate.send(KafkaTopics.NOTIFICATION_TOPIC, userId, event);
            kafkaTemplate.send(KafkaTopics.NOTIFICATION_EVENTS, userId, event);
            log.info("Published NotificationEvent to Notification-Service for userId: {}", userId);
        } catch (Exception e) {
            log.error("Failed to publish NotificationEvent for userId: {}. Error: {}", userId, e.getMessage());
        }
    }

    /**
     * Publishes Business/Shop verification approval/rejection event to Kafka topics
     * including business-operation-events for shop-service consumption!
     */
    public void publishBusinessVerifiedEvent(
            String businessId,
            String userId,
            String businessName,
            String status,
            String adminId,
            String rejectionReason,
            String notes
    ) {
        try {
            boolean isApproved = "APPROVED".equalsIgnoreCase(status);

            // 1. Publish BusinessVerifiedEvent & ShopStatusChangedEvent
            BusinessVerifiedEvent verifiedEvent = BusinessVerifiedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .businessId(businessId)
                    .shopId(businessId)
                    .userId(userId)
                    .businessName(businessName)
                    .verificationStatus(status)
                    .verifiedByAdminId(adminId)
                    .rejectionReason(rejectionReason)
                    .notes(notes)
                    .timestamp(LocalDateTime.now())
                    .build();

            kafkaTemplate.send(KafkaTopics.BUSINESS_EVENTS, businessId, verifiedEvent);
            kafkaTemplate.send(KafkaTopics.SHOP_EVENTS, businessId, verifiedEvent);

            ShopStatus shopStatus = isApproved ? ShopStatus.ACTIVE : ShopStatus.SUSPENDED;
            ShopStatusChangedEvent statusEvent = ShopStatusChangedEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("BUSINESS_VERIFICATION_UPDATE")
                    .eventTime(LocalDateTime.now())
                    .shopId(businessId)
                    .userId(userId)
                    .shopName(businessName)
                    .previousStatus(ShopStatus.PENDING_VERIFICATION)
                    .shopStatus(shopStatus)
                    .build();

            kafkaTemplate.send(KafkaTopics.SHOP_EVENTS, businessId, statusEvent);

            // 2. Publish BusinessOperation event for shop-service BusinessAccessState consumer
            if (isApproved) {
                VerificationApprovedEvent approvedEvent = VerificationApprovedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(BusinessOperationEventType.VERIFICATION_APPROVED)
                        .eventVersion(1)
                        .source("SUPER_USER")
                        .occurredAt(LocalDateTime.now())
                        .businessId(businessId)
                        .performedBy(adminId != null ? adminId : "ADMIN")
                        .description("Verification approved by Admin")
                        .build();
                kafkaTemplate.send(KafkaTopics.BUSINESS_OPERATION_EVENTS, businessId, approvedEvent);
            } else if ("REUPLOAD_REQUIRED".equalsIgnoreCase(status)) {
                VerificationReuploadRequiredEvent reuploadEvent = VerificationReuploadRequiredEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(BusinessOperationEventType.VERIFICATION_REUPLOAD_REQUIRED)
                        .eventVersion(1)
                        .source("SUPER_USER")
                        .occurredAt(LocalDateTime.now())
                        .businessId(businessId)
                        .performedBy(adminId != null ? adminId : "ADMIN")
                        .reason(rejectionReason != null ? rejectionReason : "Document reupload required")
                        .build();
                kafkaTemplate.send(KafkaTopics.BUSINESS_OPERATION_EVENTS, businessId, reuploadEvent);
            } else {
                VerificationRejectedEvent rejectedEvent = VerificationRejectedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(BusinessOperationEventType.VERIFICATION_REJECTED)
                        .eventVersion(1)
                        .source("SUPER_USER")
                        .occurredAt(LocalDateTime.now())
                        .businessId(businessId)
                        .performedBy(adminId != null ? adminId : "ADMIN")
                        .reason(rejectionReason != null ? rejectionReason : "Verification rejected by Admin")
                        .build();
                kafkaTemplate.send(KafkaTopics.BUSINESS_OPERATION_EVENTS, businessId, rejectedEvent);
            }

            log.info("Published Business Verification Events for businessId: {} with status: {}", businessId, status);

            // 3. Trigger notification to shop owner
            String notifTitle = isApproved ? "Business Verified 🎉" : "Business Verification Update";
            String notifMessage = isApproved
                    ? "Congratulations! Your business '" + businessName + "' has been verified."
                    : "Your business verification for '" + businessName + "' was not approved: " + (rejectionReason != null ? rejectionReason : "Documents require update.");

            sendNotification(userId, notifTitle, notifMessage, businessId, "SHOP", "SYSTEM", "/shop/account");

        } catch (Exception e) {
            log.error("Failed to publish BusinessVerifiedEvent for businessId: {}. Error: {}", businessId, e.getMessage());
        }
    }

    /**
     * Publishes Business capability/subscription enabled or disabled event to business-operation-events.
     */
    public void publishCapabilityEvent(String businessId, String capabilityType, String action, String adminId) {
        try {
            if ("ENABLE".equalsIgnoreCase(action)) {
                CapabilityEnabledEvent event = CapabilityEnabledEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(BusinessOperationEventType.CAPABILITY_ENABLED)
                        .eventVersion(1)
                        .source("SUPER_USER")
                        .occurredAt(LocalDateTime.now())
                        .businessId(businessId)
                        .capabilityType(capabilityType)
                        .performedBy(adminId != null ? adminId : "ADMIN")
                        .build();
                kafkaTemplate.send(KafkaTopics.BUSINESS_OPERATION_EVENTS, businessId, event);
            } else if ("SUSPEND".equalsIgnoreCase(action)) {
                CapabilitySuspendedEvent event = CapabilitySuspendedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(BusinessOperationEventType.CAPABILITY_SUSPENDED)
                        .eventVersion(1)
                        .source("SUPER_USER")
                        .occurredAt(LocalDateTime.now())
                        .businessId(businessId)
                        .capabilityType(capabilityType)
                        .performedBy(adminId != null ? adminId : "ADMIN")
                        .build();
                kafkaTemplate.send(KafkaTopics.BUSINESS_OPERATION_EVENTS, businessId, event);
            } else {
                CapabilityDisabledEvent event = CapabilityDisabledEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(BusinessOperationEventType.CAPABILITY_DISABLED)
                        .eventVersion(1)
                        .source("SUPER_USER")
                        .occurredAt(LocalDateTime.now())
                        .businessId(businessId)
                        .capabilityType(capabilityType)
                        .performedBy(adminId != null ? adminId : "ADMIN")
                        .build();
                kafkaTemplate.send(KafkaTopics.BUSINESS_OPERATION_EVENTS, businessId, event);
            }
            log.info("Published CapabilityEvent [{}] for businessId: {}, type: {}", action, businessId, capabilityType);
        } catch (Exception e) {
            log.error("Failed to publish CapabilityEvent for businessId: {}. Error: {}", businessId, e.getMessage());
        }
    }

    /**
     * Publishes payment event to Kafka.
     */
    public void publishPaymentEvent(PaymentEvent paymentEvent) {
        try {
            if (paymentEvent.getEventId() == null) {
                paymentEvent.setEventId(UUID.randomUUID().toString());
            }
            if (paymentEvent.getTimestamp() == null) {
                paymentEvent.setTimestamp(LocalDateTime.now());
            }

            kafkaTemplate.send(KafkaTopics.BILLING_EVENTS, paymentEvent.getBusinessId(), paymentEvent);
            kafkaTemplate.send(KafkaTopics.PAYMENT_EVENTS, paymentEvent.getBusinessId(), paymentEvent);
            log.info("Published PaymentEvent for paymentId: {}, businessId: {}", paymentEvent.getPaymentId(), paymentEvent.getBusinessId());
        } catch (Exception e) {
            log.error("Failed to publish PaymentEvent: {}. Error: {}", paymentEvent.getPaymentId(), e.getMessage());
        }
    }
}
