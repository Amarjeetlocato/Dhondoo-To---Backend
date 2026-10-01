package com.whoami.launch.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.locato.constants.events.business.BusinessCreatedEvent;
import com.locato.constants.events.business.BusinessDeletedEvent;
import com.locato.constants.events.business.BusinessStatusChangedEvent;
import com.locato.constants.events.business.BusinessUpdatedEvent;
import com.locato.constants.events.product.ProductCreatedEvent;
import com.locato.constants.events.product.ProductDeletedEvent;
import com.locato.constants.events.product.ProductUpdatedEvent;
import com.locato.constants.events.reel.ReelCreatedEvent;
import com.locato.constants.events.reel.ReelDeletedEvent;
import com.locato.constants.events.reel.ReelUpdatedEvent;
import com.locato.constants.events.service.ServiceCreatedEvent;
import com.locato.constants.events.service.ServiceDeletedEvent;
import com.locato.constants.events.service.ServiceUpdatedEvent;
import com.whoami.launch.dto.NotificationRequest;
import com.whoami.launch.dto.NotificationResponse;
import com.whoami.launch.dto.UnreadCountResponse;
import com.whoami.launch.entity.Notification;
import com.whoami.launch.enums.NotificationType;
import com.whoami.launch.repository.NotificationRepository;
import com.whoami.launch.service.FCMService;
import com.whoami.launch.service.NotificationService;
import com.whoami.launch.service.NotificationTemplateService;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing notifications.
 */
@Slf4j
@Service
public class NotificationServiceImpl
        implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final Optional<FCMService> fcmService;
    private final NotificationTemplateService templateService;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            Optional<FCMService> fcmService,
            NotificationTemplateService templateService) {

        this.notificationRepository = notificationRepository;
        this.fcmService = fcmService;
        this.templateService = templateService;
    }

    @Override
    @Transactional
    public NotificationResponse createNotification(
            NotificationRequest request) {

        log.info(
                "Creating notification for user: {}",
                request.getUserId()
        );

        String notificationId =
                "NOTIFICATION_" + UUID.randomUUID();

        String actionsJson =
                templateService.buildActions(
                        request.getType()
                );

        String deepLink =
                templateService.buildDeepLink(
                        request.getType(),
                        request.getTargetId()
                );

        Notification notification =
                Notification.builder()
                        .notificationId(notificationId)
                        .userId(request.getUserId())
                        .title(request.getTitle())
                        .message(request.getMessage())
                        .imageUrl(request.getImageUrl())
                        .targetId(request.getTargetId())
                        .targetType(request.getTargetType())
                        .actionsJson(actionsJson)
                        .deepLink(deepLink)
                        .metadataJson(
                                request.getMetadataJson() != null
                                        ? request.getMetadataJson()
                                        : "{}"
                        )
                        .type(request.getType())
                        .isRead(false)
                        .isDeleted(false)
                        .build();

        Notification savedNotification =
                notificationRepository.save(notification);

        log.info(
                "Notification created successfully with ID: {}",
                notificationId
        );

        NotificationResponse response =
                mapToResponse(savedNotification);

        if (Boolean.TRUE.equals(request.getSendPush())) {

            if (fcmService.isPresent()) {

                fcmService.get()
                        .sendNotificationWithRetry(
                                request.getUserId(),
                                response,
                                3
                        );

            } else {

                log.warn(
                        "FCM service not available, skipping push notification for user: {}",
                        request.getUserId()
                );
            }
        }

        return response;
    }

    @Override
    public Page<NotificationResponse> getNotifications(
            String userId,
            Pageable pageable) {

        log.info(
                "Fetching notifications for user: {} with pagination",
                userId
        );

        return notificationRepository
                .findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(
                        userId,
                        pageable
                )
                .map(this::mapToResponse);
    }

    @Override
    public Page<NotificationResponse> getUnreadNotifications(
            String userId,
            Pageable pageable) {

        log.info(
                "Fetching unread notifications for user: {}",
                userId
        );

        return notificationRepository
                .findByUserIdAndIsReadFalseAndIsDeletedFalseOrderByCreatedAtDesc(
                        userId,
                        pageable
                )
                .map(this::mapToResponse);
    }

    @Override
    public Page<NotificationResponse> getNotificationsByType(
            String userId,
            String type,
            Pageable pageable) {

        log.info(
                "Fetching notifications for user: {} with type: {}",
                userId,
                type
        );

        NotificationType notificationType =
                Enum.valueOf(
                        NotificationType.class,
                        type.toUpperCase()
                );

        return notificationRepository
                .findByUserIdAndTypeAndIsDeletedFalseOrderByCreatedAtDesc(
                        userId,
                        notificationType,
                        pageable
                )
                .map(this::mapToResponse);
    }

    @Override
    public UnreadCountResponse getUnreadCount(
            String userId) {

        log.info(
                "Fetching unread count for user: {}",
                userId
        );

        Long unreadCount =
                notificationRepository
                        .countUnreadNotifications(userId);

        Long totalCount =
                notificationRepository
                        .countTotalNotifications(userId);

        return UnreadCountResponse.builder()
                .userId(userId)
                .unreadCount(unreadCount)
                .totalCount(totalCount)
                .build();
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(
            String notificationId) {

        log.info(
                "Marking notification as read: {}",
                notificationId
        );

        Notification notification =
                notificationRepository
                        .findByNotificationId(notificationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found: "
                                                + notificationId
                                )
                        );

        notificationRepository.markAsRead(notificationId);

        notification.setIsRead(true);

        return mapToResponse(notification);
    }

    @Override
    @Transactional
    public void markAllAsRead(String userId) {

        log.info(
                "Marking all notifications as read for user: {}",
                userId
        );

        notificationRepository.markAllAsRead(userId);
    }

    @Override
    @Transactional
    public void deleteNotification(
            String notificationId) {

        log.info(
                "Deleting notification: {}",
                notificationId
        );

        notificationRepository
                .findByNotificationId(notificationId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Notification not found: "
                                        + notificationId
                        )
                );

        notificationRepository
                .softDeleteById(notificationId);
    }

    @Override
    public NotificationResponse getNotificationById(
            String notificationId) {

        log.info(
                "Fetching notification: {}",
                notificationId
        );

        Notification notification =
                notificationRepository
                        .findByNotificationId(notificationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found: "
                                                + notificationId
                                )
                        );

        return mapToResponse(notification);
    }

    @Override
    public boolean hasUnreadNotifications(
            String userId) {

        return notificationRepository
                .hasUnreadNotifications(userId);
    }

    private NotificationResponse mapToResponse(
            Notification notification) {

        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .userId(notification.getUserId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .imageUrl(notification.getImageUrl())
                .targetId(notification.getTargetId())
                .targetType(notification.getTargetType())
                .type(notification.getType())
                .isRead(notification.getIsRead())
                .metadataJson(notification.getMetadataJson())
                .actionsJson(notification.getActionsJson())
                .deepLink(notification.getDeepLink())
                .isDeleted(notification.getIsDeleted())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }

    @Override
    public void handleBusinessCreated(
            BusinessCreatedEvent event) {

        String metadataJson = """
                {
                  "businessName":"%s",
                  "logoUrl":"%s",
                  "bannerUrl":"%s",
                  "eventType":"%s",
                  "eventId":"%s"
                }
                """.formatted(
                event.getBusinessName(),
                event.getLogoUrl(),
                event.getBannerUrl(),
                event.getEventType(),
                event.getEventId()
        );

        NotificationRequest request =
                NotificationRequest.builder()
                        .userId(event.getUserId())
                        .title("Business Created")
                        .message(
                                "Your business '"
                                        + event.getBusinessName()
                                        + "' has been created successfully."
                        )
                        .targetId(event.getBusinessId())
                        .targetType("BUSINESS")
                        .imageUrl(event.getLogoUrl())
                        .metadataJson(metadataJson)
                        .type(NotificationType.SHOP)
                        .sendPush(true)
                        .build();

        createNotification(request);
    }
    
    @Override
    public void handleBusinessUpdated(
            BusinessUpdatedEvent event) {

        String metadataJson = """
                {
                  "businessName":"%s",
                  "logoUrl":"%s",
                  "bannerUrl":"%s",
                  "changes":"%s",
                  "eventType":"%s",
                  "eventId":"%s"
                }
                """.formatted(
                event.getBusinessName(),
                event.getLogoUrl(),
                event.getBannerUrl(),
                event.getChanges(),
                event.getEventType(),
                event.getEventId()
        );

        NotificationRequest request =
                NotificationRequest.builder()
                        .userId(event.getUserId())
                        .title("Business Updated")
                        .message(
                                "Your business '"
                                        + event.getBusinessName()
                                        + "' has been updated."
                        )
                        .targetId(event.getBusinessId())
                        .targetType("BUSINESS")
                        .imageUrl(event.getLogoUrl())
                        .metadataJson(metadataJson)
                        .type(NotificationType.SHOP)
                        .sendPush(true)
                        .build();

        createNotification(request);
    }
    
    @Override
    public void handleBusinessStatusChanged(
            BusinessStatusChangedEvent event) {

        String metadataJson = """
                {
                  "businessName":"%s",
                  "previousStatus":"%s",
                  "businessStatus":"%s",
                  "eventType":"%s",
                  "eventId":"%s"
                }
                """.formatted(
                event.getBusinessName(),
                event.getPreviousStatus(),
                event.getBusinessStatus(),
                event.getEventType(),
                event.getEventId()
        );

        NotificationRequest request =
                NotificationRequest.builder()
                        .userId(event.getUserId())
                        .title("Business Status Updated")
                        .message(
                                "Your business '"
                                        + event.getBusinessName()
                                        + "' status changed from "
                                        + event.getPreviousStatus()
                                        + " to "
                                        + event.getBusinessStatus()
                                        + "."
                        )
                        .targetId(event.getBusinessId())
                        .targetType("BUSINESS")
                        .imageUrl(event.getLogoUrl())
                        .metadataJson(metadataJson)
                        .type(NotificationType.SHOP)
                        .sendPush(true)
                        .build();

        createNotification(request);
    }
    
    @Override
    public void handleBusinessDeleted(
            BusinessDeletedEvent event) {

        String metadataJson = """
                {
                  "businessName":"%s",
                  "eventType":"%s",
                  "eventId":"%s"
                }
                """.formatted(
                event.getBusinessName(),
                event.getEventType(),
                event.getEventId()
        );

        NotificationRequest request =
                NotificationRequest.builder()
                        .userId(event.getUserId())
                        .title("Business Deleted")
                        .message(
                                "Your business '"
                                        + event.getBusinessName()
                                        + "' has been deleted."
                        )
                        .targetId(event.getBusinessId())
                        .targetType("BUSINESS")
                        .imageUrl(event.getLogoUrl())
                        .metadataJson(metadataJson)
                        .type(NotificationType.SHOP)
                        .sendPush(true)
                        .build();

        createNotification(request);
    }
    
    @Override
    public void handleProductCreated(
            ProductCreatedEvent event) {

        String imageUrl = null;

        if (event.getProductImages() != null
                && !event.getProductImages().isEmpty()) {

            imageUrl = event.getProductImages().get(0);
        }

        String metadataJson = """
                {
                  "productName":"%s",
                  "productPrice":"%s",
                  "businessName":"%s",
                  "businessLogo":"%s",
                  "businessBanner":"%s",
                  "eventType":"%s",
                  "eventId":"%s"
                }
                """.formatted(
                event.getProductName(),
                event.getProductPrice(),
                event.getBusinessName(),
                event.getBusinessLogo(),
                event.getBusinessBanner(),
                event.getEventType(),
                event.getEventId()
        );

        NotificationRequest request =
                NotificationRequest.builder()
                        .userId(event.getUserId())
                        .title("Product Created")
                        .message(
                                "Your product '"
                                        + event.getProductName()
                                        + "' has been created successfully."
                        )
                        .targetId(event.getProductId())
                        .targetType("PRODUCT")
                        .imageUrl(imageUrl)
                        .metadataJson(metadataJson)
                        .type(NotificationType.PRODUCT)
                        .sendPush(true)
                        .build();

        createNotification(request);
    }
    
    
    @Override
    public void handleProductUpdated(
            ProductUpdatedEvent event) {

        String imageUrl = null;

        if (event.getProductImages() != null
                && !event.getProductImages().isEmpty()) {

            imageUrl = event.getProductImages().get(0);
        }

        String metadataJson = """
                {
                  "productName":"%s",
                  "productPrice":"%s",
                  "businessName":"%s",
                  "businessLogo":"%s",
                  "businessBanner":"%s",
                  "changes":"%s",
                  "eventType":"%s",
                  "eventId":"%s"
                }
                """.formatted(
                event.getProductName(),
                event.getProductPrice(),
                event.getBusinessName(),
                event.getBusinessLogo(),
                event.getBusinessBanner(),
                event.getChanges(),
                event.getEventType(),
                event.getEventId()
        );

        NotificationRequest request =
                NotificationRequest.builder()
                        .userId(event.getUserId())
                        .title("Product Updated")
                        .message(
                                "Your product '"
                                        + event.getProductName()
                                        + "' has been updated."
                        )
                        .targetId(event.getProductId())
                        .targetType("PRODUCT")
                        .imageUrl(imageUrl)
                        .metadataJson(metadataJson)
                        .type(NotificationType.PRODUCT)
                        .sendPush(true)
                        .build();

        createNotification(request);
    }
    
    @Override
    public void handleProductDeleted(
            ProductDeletedEvent event) {

        String imageUrl = null;

        if (event.getProductImages() != null
                && !event.getProductImages().isEmpty()) {

            imageUrl = event.getProductImages().get(0);
        }

        String metadataJson = """
                {
                  "productName":"%s",
                  "businessName":"%s",
                  "businessLogo":"%s",
                  "businessBanner":"%s",
                  "eventType":"%s",
                  "eventId":"%s"
                }
                """.formatted(
                event.getProductName(),
                event.getBusinessName(),
                event.getBusinessLogo(),
                event.getBusinessBanner(),
                event.getEventType(),
                event.getEventId()
        );

        NotificationRequest request =
                NotificationRequest.builder()
                        .userId(event.getUserId())
                        .title("Product Deleted")
                        .message(
                                "Your product '"
                                        + event.getProductName()
                                        + "' has been deleted."
                        )
                        .targetId(event.getProductId())
                        .targetType("PRODUCT")
                        .imageUrl(imageUrl)
                        .metadataJson(metadataJson)
                        .type(NotificationType.PRODUCT)
                        .sendPush(true)
                        .build();

        createNotification(request);
    }
    
    @Override
    public void handleServiceCreated(
            ServiceCreatedEvent event) {

        String metadataJson = """
                {
                  "serviceDescription":"%s",
                  "price":"%s",
                  "businessName":"%s",
                  "businessLogo":"%s",
                  "businessBanner":"%s",
                  "eventType":"%s",
                  "eventId":"%s"
                }
                """.formatted(
                event.getServiceDescription(),
                event.getPrice(),
                event.getBusinessName(),
                event.getBusinessLogo(),
                event.getBusinessBanner(),
                event.getEventType(),
                event.getEventId()
        );

        NotificationRequest request =
                NotificationRequest.builder()
                        .userId(event.getUserId())
                        .title("Service Created")
                        .message(
                                "Your service '"
                                        + event.getServiceName()
                                        + "' has been created successfully."
                        )
                        .targetId(event.getServiceId())
                        .targetType("SERVICE")
                        .imageUrl(event.getServiceThumbnail())
                        .metadataJson(metadataJson)
                        .type(NotificationType.SERVICE)
                        .sendPush(true)
                        .build();

        createNotification(request);
    }
    
    @Override
    public void handleServiceUpdated(
            ServiceUpdatedEvent event) {

        String metadataJson = """
                {
                  "serviceDescription":"%s",
                  "price":"%s",
                  "businessName":"%s",
                  "businessLogo":"%s",
                  "businessBanner":"%s",
                  "changes":"%s",
                  "eventType":"%s",
                  "eventId":"%s"
                }
                """.formatted(
                event.getServiceDescription(),
                event.getPrice(),
                event.getBusinessName(),
                event.getBusinessLogo(),
                event.getBusinessBanner(),
                event.getChanges(),
                event.getEventType(),
                event.getEventId()
        );

        NotificationRequest request =
                NotificationRequest.builder()
                        .userId(event.getUserId())
                        .title("Service Updated")
                        .message(
                                "Your service '"
                                        + event.getServiceName()
                                        + "' has been updated."
                        )
                        .targetId(event.getServiceId())
                        .targetType("SERVICE")
                        .imageUrl(event.getServiceThumbnail())
                        .metadataJson(metadataJson)
                        .type(NotificationType.SERVICE)
                        .sendPush(true)
                        .build();

        createNotification(request);
    }
    
    @Override
    public void handleServiceDeleted(
            ServiceDeletedEvent event) {

        String metadataJson = """
                {
                  "businessName":"%s",
                  "businessLogo":"%s",
                  "businessBanner":"%s",
                  "eventType":"%s",
                  "eventId":"%s"
                }
                """.formatted(
                event.getBusinessName(),
                event.getBusinessLogo(),
                event.getBusinessBanner(),
                event.getEventType(),
                event.getEventId()
        );

        NotificationRequest request =
                NotificationRequest.builder()
                        .userId(event.getUserId())
                        .title("Service Deleted")
                        .message(
                                "Your service '"
                                        + event.getServiceName()
                                        + "' has been deleted."
                        )
                        .targetId(event.getServiceId())
                        .targetType("SERVICE")
                        .imageUrl(event.getServiceThumbnail())
                        .metadataJson(metadataJson)
                        .type(NotificationType.SERVICE)
                        .sendPush(true)
                        .build();

        createNotification(request);
    }
    

    @Override
    public void handleReelCreated(
            ReelCreatedEvent event) {

        String metadataJson = """
                {
                  "reelVideo":"%s",
                  "businessName":"%s",
                  "businessLogo":"%s",
                  "businessBanner":"%s",
                  "eventType":"%s",
                  "eventId":"%s"
                }
                """.formatted(
                event.getReelVideo(),
                event.getBusinessName(),
                event.getBusinessLogo(),
                event.getBusinessBanner(),
                event.getEventType(),
                event.getEventId()
        );

        NotificationRequest request =
                NotificationRequest.builder()
                        .userId(event.getUserId())
                        .title("Reel Created")
                        .message(
                                "Your reel for '"
                                        + event.getBusinessName()
                                        + "' has been created successfully."
                        )
                        .targetId(event.getReelId())
                        .targetType("REEL")
                        .imageUrl(event.getReelThumbnail())
                        .metadataJson(metadataJson)
                        .type(NotificationType.REEL)
                        .sendPush(true)
                        .build();

        createNotification(request);
    }
    
    @Override
    public void handleReelUpdated(
            ReelUpdatedEvent event) {

        String metadataJson = """
                {
                  "reelVideo":"%s",
                  "businessName":"%s",
                  "businessLogo":"%s",
                  "businessBanner":"%s",
                  "changes":"%s",
                  "eventType":"%s",
                  "eventId":"%s"
                }
                """.formatted(
                event.getReelVideo(),
                event.getBusinessName(),
                event.getBusinessLogo(),
                event.getBusinessBanner(),
                event.getChanges(),
                event.getEventType(),
                event.getEventId()
        );

        NotificationRequest request =
                NotificationRequest.builder()
                        .userId(event.getUserId())
                        .title("Reel Updated")
                        .message(
                                "Your reel for '"
                                        + event.getBusinessName()
                                        + "' has been updated."
                        )
                        .targetId(event.getReelId())
                        .targetType("REEL")
                        .imageUrl(event.getReelThumbnail())
                        .metadataJson(metadataJson)
                        .type(NotificationType.REEL)
                        .sendPush(true)
                        .build();

        createNotification(request);
    }
    
    
    @Override
    public void handleReelDeleted(
            ReelDeletedEvent event) {

        String metadataJson = """
                {
                  "businessName":"%s",
                  "businessLogo":"%s",
                  "businessBanner":"%s",
                  "eventType":"%s",
                  "eventId":"%s"
                }
                """.formatted(
                event.getBusinessName(),
                event.getBusinessLogo(),
                event.getBusinessBanner(),
                event.getEventType(),
                event.getEventId()
        );

        NotificationRequest request =
                NotificationRequest.builder()
                        .userId(event.getUserId())
                        .title("Reel Deleted")
                        .message(
                                "Your reel for '"
                                        + event.getBusinessName()
                                        + "' has been deleted."
                        )
                        .targetId(event.getReelId())
                        .targetType("REEL")
                        .imageUrl(event.getReelThumbnail())
                        .metadataJson(metadataJson)
                        .type(NotificationType.REEL)
                        .sendPush(true)
                        .build();

        createNotification(request);
    }
}