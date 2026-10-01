package com.whoami.launch.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

public interface NotificationService {

    NotificationResponse createNotification(
            NotificationRequest request);

    Page<NotificationResponse> getNotifications(
            String userId,
            Pageable pageable);

    Page<NotificationResponse> getUnreadNotifications(
            String userId,
            Pageable pageable);

    Page<NotificationResponse> getNotificationsByType(
            String userId,
            String type,
            Pageable pageable);

    UnreadCountResponse getUnreadCount(String userId);

    NotificationResponse markAsRead(String notificationId);

    void markAllAsRead(String userId);

    void deleteNotification(String notificationId);

    NotificationResponse getNotificationById(String notificationId);

    boolean hasUnreadNotifications(String userId);

    void handleBusinessCreated(BusinessCreatedEvent event);

    void handleBusinessUpdated(BusinessUpdatedEvent event);

    void handleBusinessStatusChanged(BusinessStatusChangedEvent event);

    void handleBusinessDeleted(BusinessDeletedEvent event);

    void handleProductCreated(ProductCreatedEvent event);

    void handleProductUpdated(ProductUpdatedEvent event);

    void handleProductDeleted(ProductDeletedEvent event);

    void handleServiceCreated(ServiceCreatedEvent event);

    void handleServiceUpdated(ServiceUpdatedEvent event);

    void handleServiceDeleted(ServiceDeletedEvent event);

    void handleReelCreated(ReelCreatedEvent event);

    void handleReelUpdated(ReelUpdatedEvent event);

    void handleReelDeleted(ReelDeletedEvent event);
}