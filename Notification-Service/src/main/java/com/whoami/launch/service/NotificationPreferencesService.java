package com.whoami.launch.service;

import com.whoami.launch.dto.NotificationPreferencesRequest;
import com.whoami.launch.dto.NotificationPreferencesResponse;
import com.whoami.launch.entity.NotificationPreferences;

public interface NotificationPreferencesService {

    NotificationPreferencesResponse getPreferences(String userId);

    NotificationPreferences createDefaultPreferences(String userId);

    NotificationPreferencesResponse updatePreferences(
            String userId,
            NotificationPreferencesRequest request);

    boolean isNotificationTypeEnabled(
            String userId,
            String notificationType);

    NotificationPreferencesResponse resetToDefault(String userId);
}