package com.whoami.launch.service;

import com.whoami.launch.dto.NotificationPreferencesRequest;
import com.whoami.launch.dto.NotificationPreferencesResponse;
import com.whoami.launch.entity.NotificationPreferences;
import com.whoami.launch.repository.NotificationPreferencesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing user notification preferences
 */
@Slf4j
@Service
public class NotificationPreferencesService {

    private final NotificationPreferencesRepository preferencesRepository;

    public NotificationPreferencesService(NotificationPreferencesRepository preferencesRepository) {
        this.preferencesRepository = preferencesRepository;
    }

    /**
     * Get preferences for a user, creating default if not exists
     */
    public NotificationPreferencesResponse getPreferences(String userId) {
        log.info("Fetching notification preferences for user: {}", userId);

        var preferences = preferencesRepository.findByUserId(userId)
                .orElseGet(() -> {
                    NotificationPreferences newPrefs = new NotificationPreferences();
                    newPrefs.setUserId(userId);
                    newPrefs.setOrderNotification(true);
                    newPrefs.setChatNotification(true);
                    newPrefs.setPromotionNotification(true);
                    newPrefs.setReelNotification(true);
                    newPrefs.setProductNotification(true);
                    newPrefs.setShopNotification(true);
                    newPrefs.setServiceNotification(true);
                    newPrefs.setAdminNotification(true);
                    newPrefs.setFollowNotification(true);
                    return preferencesRepository.save(newPrefs);
                });

        return mapToResponse(preferences);
    }

    /**
     * Create default preferences for a new user
     */
    @Transactional
    public NotificationPreferences createDefaultPreferences(String userId) {
        log.info("Creating default notification preferences for user: {}", userId);

        NotificationPreferences preferences = NotificationPreferences.builder()
                .userId(userId)
                .orderNotification(true)
                .chatNotification(true)
                .promotionNotification(true)
                .reelNotification(true)
                .productNotification(true)
                .shopNotification(true)
                .serviceNotification(true)
                .adminNotification(true)
                .followNotification(true)
                .build();

        return preferencesRepository.save(preferences);
    }

    /**
     * Update preferences for a user
     */
    @Transactional
    public NotificationPreferencesResponse updatePreferences(String userId, NotificationPreferencesRequest request) {
        log.info("Updating notification preferences for user: {}", userId);

        NotificationPreferences preferences = preferencesRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreferencesEntity(userId));

        // Update only provided fields
        if (request.getOrderNotification() != null) {
            preferences.setOrderNotification(request.getOrderNotification());
        }
        if (request.getChatNotification() != null) {
            preferences.setChatNotification(request.getChatNotification());
        }
        if (request.getPromotionNotification() != null) {
            preferences.setPromotionNotification(request.getPromotionNotification());
        }
        if (request.getReelNotification() != null) {
            preferences.setReelNotification(request.getReelNotification());
        }
        if (request.getProductNotification() != null) {
            preferences.setProductNotification(request.getProductNotification());
        }
        if (request.getShopNotification() != null) {
            preferences.setShopNotification(request.getShopNotification());
        }
        if (request.getServiceNotification() != null) {
            preferences.setServiceNotification(request.getServiceNotification());
        }
        if (request.getAdminNotification() != null) {
            preferences.setAdminNotification(request.getAdminNotification());
        }
        if (request.getFollowNotification() != null) {
            preferences.setFollowNotification(request.getFollowNotification());
        }

        NotificationPreferences updated = preferencesRepository.save(preferences);
        return mapToResponse(updated);
    }

    /**
     * Check if a specific notification type is enabled for a user
     */
    public boolean isNotificationTypeEnabled(String userId, String notificationType) {
        var preferences = preferencesRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreferencesEntity(userId));

        return switch (notificationType.toLowerCase()) {
            case "order" -> preferences.getOrderNotification();
            case "chat" -> preferences.getChatNotification();
            case "promotion" -> preferences.getPromotionNotification();
            case "reel" -> preferences.getReelNotification();
            case "product" -> preferences.getProductNotification();
            case "shop" -> preferences.getShopNotification();
            case "service" -> preferences.getServiceNotification();
            case "admin" -> preferences.getAdminNotification();
            case "follow" -> preferences.getFollowNotification();
            default -> true;
        };
    }

    /**
     * Reset preferences to default for a user
     */
    @Transactional
    public NotificationPreferencesResponse resetToDefault(String userId) {
        log.info("Resetting notification preferences to default for user: {}", userId);

        var preferences = preferencesRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultPreferencesEntity(userId));

        preferences.setOrderNotification(true);
        preferences.setChatNotification(true);
        preferences.setPromotionNotification(true);
        preferences.setReelNotification(true);
        preferences.setProductNotification(true);
        preferences.setShopNotification(true);
        preferences.setServiceNotification(true);
        preferences.setAdminNotification(true);
        preferences.setFollowNotification(true);

        NotificationPreferences updated = preferencesRepository.save(preferences);
        return mapToResponse(updated);
    }

    /**
     * Create default preferences entity without saving
     */
    private NotificationPreferences createDefaultPreferencesEntity(String userId) {
        return NotificationPreferences.builder()
                .userId(userId)
                .orderNotification(true)
                .chatNotification(true)
                .promotionNotification(true)
                .reelNotification(true)
                .productNotification(true)
                .shopNotification(true)
                .serviceNotification(true)
                .adminNotification(true)
                .followNotification(true)
                .build();
    }

    /**
     * Map entity to response DTO
     */
    private NotificationPreferencesResponse mapToResponse(NotificationPreferences preferences) {
        return NotificationPreferencesResponse.builder()
                .preferenceId(preferences.getPreferenceId())
                .userId(preferences.getUserId())
                .orderNotification(preferences.getOrderNotification())
                .chatNotification(preferences.getChatNotification())
                .promotionNotification(preferences.getPromotionNotification())
                .reelNotification(preferences.getReelNotification())
                .productNotification(preferences.getProductNotification())
                .shopNotification(preferences.getShopNotification())
                .serviceNotification(preferences.getServiceNotification())
                .adminNotification(preferences.getAdminNotification())
                .followNotification(preferences.getFollowNotification())
                .createdAt(preferences.getCreatedAt())
                .updatedAt(preferences.getUpdatedAt())
                .build();
    }
}
