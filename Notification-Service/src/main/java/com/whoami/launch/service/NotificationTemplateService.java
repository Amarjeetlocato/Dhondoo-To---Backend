package com.whoami.launch.service;

import com.whoami.launch.enums.NotificationType;

public interface NotificationTemplateService {

    String buildActions(NotificationType type);

    String buildDeepLink(
            NotificationType type,
            String targetId);
}
