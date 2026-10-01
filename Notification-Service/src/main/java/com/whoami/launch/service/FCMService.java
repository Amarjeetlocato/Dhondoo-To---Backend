package com.whoami.launch.service;

import com.whoami.launch.dto.NotificationResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface FCMService {

    CompletableFuture<Boolean> sendPushNotificationToUser(
            String userId,
            NotificationResponse notification);

    void sendPushNotificationToToken(
            String deviceToken,
            NotificationResponse notification);

    CompletableFuture<Boolean> sendPushNotificationToUsers(
            List<String> userIds,
            NotificationResponse notification);

    CompletableFuture<Boolean> sendNotificationWithRetry(
            String userId,
            NotificationResponse notification,
            int maxRetries);
}