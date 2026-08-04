package com.whoami.launch.util;

import com.whoami.launch.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private String userId;

    private String title;

    private String message;

    private String imageUrl;

    private String targetId;

    private String targetType;

    private NotificationType type;

    private Boolean sendPush;

    // Optional fields
    private String metadataJson;

    private String actionsJson;

    private String deepLink;
}