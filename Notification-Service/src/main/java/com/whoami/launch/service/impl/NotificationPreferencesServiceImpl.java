package com.whoami.launch.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.whoami.launch.dto.NotificationPreferencesRequest;
import com.whoami.launch.dto.NotificationPreferencesResponse;
import com.whoami.launch.entity.NotificationPreferences;
import com.whoami.launch.repository.NotificationPreferencesRepository;
import com.whoami.launch.service.NotificationPreferencesService;

import lombok.extern.slf4j.Slf4j;

/**
 * Service for managing user notification preferences.
 */
@Slf4j
@Service
public class NotificationPreferencesServiceImpl
        implements NotificationPreferencesService {

    private final NotificationPreferencesRepository preferencesRepository;

    public NotificationPreferencesServiceImpl(
            NotificationPreferencesRepository preferencesRepository) {
        this.preferencesRepository = preferencesRepository;
    }

    @Override
    public NotificationPreferencesResponse getPreferences(String userId) {

        log.info(
                "Fetching notification preferences for user: {}",
                userId
        );

        NotificationPreferences preferences =
                preferencesRepository.findByUserId(userId)
                        .orElseGet(() ->
                                preferencesRepository.save(
                                        createDefaultPreferencesEntity(userId)
                                )
                        );

        return mapToResponse(preferences);
    }

    @Override
    @Transactional
    public NotificationPreferences createDefaultPreferences(String userId) {

        log.info(
                "Creating default notification preferences for user: {}",
                userId
        );

        NotificationPreferences preferences =
                createDefaultPreferencesEntity(userId);

        return preferencesRepository.save(preferences);
    }

    @Override
    @Transactional
    public NotificationPreferencesResponse updatePreferences(
            String userId,
            NotificationPreferencesRequest request) {

        log.info(
                "Updating notification preferences for user: {}",
                userId
        );

        NotificationPreferences preferences =
                preferencesRepository.findByUserId(userId)
                        .orElseGet(() ->
                                createDefaultPreferencesEntity(userId)
                        );

        if (request.getOrderNotification() != null) {
            preferences.setOrderNotification(
                    request.getOrderNotification()
            );
        }

        if (request.getChatNotification() != null) {
            preferences.setChatNotification(
                    request.getChatNotification()
            );
        }

        if (request.getPromotionNotification() != null) {
            preferences.setPromotionNotification(
                    request.getPromotionNotification()
            );
        }

        if (request.getReelNotification() != null) {
            preferences.setReelNotification(
                    request.getReelNotification()
            );
        }

        if (request.getProductNotification() != null) {
            preferences.setProductNotification(
                    request.getProductNotification()
            );
        }

        if (request.getBusinessNotification() != null) {
            preferences.setShopNotification(
                    request.getBusinessNotification()
            );
        }

        if (request.getServiceNotification() != null) {
            preferences.setServiceNotification(
                    request.getServiceNotification()
            );
        }

        if (request.getAdminNotification() != null) {
            preferences.setAdminNotification(
                    request.getAdminNotification()
            );
        }

        if (request.getFollowNotification() != null) {
            preferences.setFollowNotification(
                    request.getFollowNotification()
            );
        }

        NotificationPreferences updated =
                preferencesRepository.save(preferences);

        return mapToResponse(updated);
    }

    @Override
    public boolean isNotificationTypeEnabled(
            String userId,
            String notificationType) {

        NotificationPreferences preferences =
                preferencesRepository.findByUserId(userId)
                        .orElseGet(() ->
                                preferencesRepository.save(
                                        createDefaultPreferencesEntity(userId)
                                )
                        );

        return switch (notificationType.toLowerCase()) {

            case "order" ->
                    preferences.getOrderNotification();

            case "chat" ->
                    preferences.getChatNotification();

            case "promotion" ->
                    preferences.getPromotionNotification();

            case "reel" ->
                    preferences.getReelNotification();

            case "product" ->
                    preferences.getProductNotification();

            case "shop" ->
                    preferences.getShopNotification();

            case "service" ->
                    preferences.getServiceNotification();

            case "admin" ->
                    preferences.getAdminNotification();

            case "follow" ->
                    preferences.getFollowNotification();

            default -> true;
        };
    }

    @Override
    @Transactional
    public NotificationPreferencesResponse resetToDefault(
            String userId) {

        log.info(
                "Resetting notification preferences to default for user: {}",
                userId
        );

        NotificationPreferences preferences =
                preferencesRepository.findByUserId(userId)
                        .orElseGet(() ->
                                createDefaultPreferencesEntity(userId)
                        );

        preferences.setOrderNotification(true);
        preferences.setChatNotification(true);
        preferences.setPromotionNotification(true);
        preferences.setReelNotification(true);
        preferences.setProductNotification(true);
        preferences.setShopNotification(true);
        preferences.setServiceNotification(true);
        preferences.setAdminNotification(true);
        preferences.setFollowNotification(true);

        NotificationPreferences updated =
                preferencesRepository.save(preferences);

        return mapToResponse(updated);
    }

    private NotificationPreferences createDefaultPreferencesEntity(
            String userId) {

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

    private NotificationPreferencesResponse mapToResponse(
            NotificationPreferences preferences) {

        return NotificationPreferencesResponse.builder()
                .preferenceId(preferences.getPreferenceId())
                .userId(preferences.getUserId())
                .orderNotification(
                        preferences.getOrderNotification()
                )
                .chatNotification(
                        preferences.getChatNotification()
                )
                .promotionNotification(
                        preferences.getPromotionNotification()
                )
                .reelNotification(
                        preferences.getReelNotification()
                )
                .productNotification(
                        preferences.getProductNotification()
                )
                .businessNotification(
                        preferences.getShopNotification()
                )
                .serviceNotification(
                        preferences.getServiceNotification()
                )
                .adminNotification(
                        preferences.getAdminNotification()
                )
                .followNotification(
                        preferences.getFollowNotification()
                )
                .createdAt(preferences.getCreatedAt())
                .updatedAt(preferences.getUpdatedAt())
                .build();
    }
}