package com.whoami.launch.service.impl;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import com.whoami.launch.dto.NotificationResponse;
import com.whoami.launch.service.DeviceTokenService;
import com.whoami.launch.service.FCMService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnBean(FirebaseMessaging.class)
public class FCMServiceImpl implements FCMService {

    private final FirebaseMessaging firebaseMessaging;
    private final DeviceTokenService deviceTokenService;

    public FCMServiceImpl(
            FirebaseMessaging firebaseMessaging,
            DeviceTokenService deviceTokenService) {

        this.firebaseMessaging = firebaseMessaging;
        this.deviceTokenService = deviceTokenService;
    }

    @Override
    public CompletableFuture<Boolean> sendPushNotificationToUser(
            String userId,
            NotificationResponse notification) {

        return CompletableFuture.supplyAsync(() -> {

            try {

                var deviceTokens =
                        deviceTokenService
                                .getActiveDeviceTokens(userId);

                if (deviceTokens.isEmpty()) {

                    log.warn(
                            "No active device tokens found for user: {}",
                            userId
                    );

                    return false;
                }

                for (var token : deviceTokens) {

                    sendPushNotificationToToken(
                            token.getDeviceToken(),
                            notification
                    );
                }

                return true;

            } catch (Exception e) {

                log.error(
                        "Error sending push notification to user: {}",
                        userId,
                        e
                );

                return false;
            }
        });
    }

    @Override
    public void sendPushNotificationToToken(
            String deviceToken,
            NotificationResponse notification) {

        try {

            Message message =
                    Message.builder()

                            .setNotification(
                                    Notification.builder()
                                            .setTitle(
                                                    notification.getTitle()
                                            )
                                            .setBody(
                                                    notification.getMessage()
                                            )
                                            .build()
                            )

                            .putData(
                                    "notificationId",
                                    notification.getNotificationId() != null
                                            ? notification.getNotificationId()
                                            : ""
                            )

                            .putData(
                                    "imageUrl",
                                    notification.getImageUrl() != null
                                            ? notification.getImageUrl()
                                            : ""
                            )

                            .putData(
                                    "targetId",
                                    notification.getTargetId() != null
                                            ? notification.getTargetId()
                                            : ""
                            )

                            .putData(
                                    "targetType",
                                    notification.getTargetType() != null
                                            ? notification.getTargetType()
                                            : ""
                            )

                            .putData(
                                    "type",
                                    notification.getType() != null
                                            ? notification.getType().toString()
                                            : ""
                            )

                            .putData(
                                    "metadataJson",
                                    notification.getMetadataJson() != null
                                            ? notification.getMetadataJson()
                                            : "{}"
                            )

                            .putData(
                                    "actionsJson",
                                    notification.getActionsJson() != null
                                            ? notification.getActionsJson()
                                            : "[]"
                            )

                            .putData(
                                    "deepLink",
                                    notification.getDeepLink() != null
                                            ? notification.getDeepLink()
                                            : ""
                            )

                            .setToken(deviceToken)
                            .build();

            String messageId =
                    firebaseMessaging.send(message);

            log.info(
                    "Successfully sent notification. " +
                    "notificationId={}, messageId={}, token={}",
                    notification.getNotificationId(),
                    messageId,
                    deviceToken
            );

        } catch (Exception e) {

            log.error(
                    "Error sending push notification. " +
                    "notificationId={}, token={}",
                    notification.getNotificationId(),
                    deviceToken,
                    e
            );

            deviceTokenService
                    .deactivateDeviceTokenByString(
                            deviceToken
                    );
        }
    }

    @Override
    public CompletableFuture<Boolean> sendPushNotificationToUsers(
            List<String> userIds,
            NotificationResponse notification) {

        return CompletableFuture.supplyAsync(() -> {

            try {

                var deviceTokens =
                        deviceTokenService
                                .getActiveDeviceTokensForUsers(
                                        userIds
                                );

                if (deviceTokens.isEmpty()) {

                    log.warn(
                            "No active device tokens found for users"
                    );

                    return false;
                }

                List<String> tokens =
                        deviceTokens.stream()
                                .map(token ->
                                        token.getDeviceToken()
                                )
                                .toList();

                sendMulticastNotification(
                        tokens,
                        notification
                );

                return true;

            } catch (Exception e) {

                log.error(
                        "Error sending multicast push notification",
                        e
                );

                return false;
            }
        });
    }

    private void sendMulticastNotification(
            List<String> deviceTokens,
            NotificationResponse notification) {

        try {

            MulticastMessage.Builder builder =
                    MulticastMessage.builder()

                            .setNotification(
                                    Notification.builder()
                                            .setTitle(
                                                    notification.getTitle()
                                            )
                                            .setBody(
                                                    notification.getMessage()
                                            )
                                            .build()
                            )

                            .putData(
                                    "notificationId",
                                    notification.getNotificationId() == null
                                            ? ""
                                            : notification.getNotificationId()
                            )

                            .putData(
                                    "imageUrl",
                                    notification.getImageUrl() == null
                                            ? ""
                                            : notification.getImageUrl()
                            )

                            .putData(
                                    "targetId",
                                    notification.getTargetId() == null
                                            ? ""
                                            : notification.getTargetId()
                            )

                            .putData(
                                    "targetType",
                                    notification.getTargetType() == null
                                            ? ""
                                            : notification.getTargetType()
                            )

                            .putData(
                                    "type",
                                    notification.getType() == null
                                            ? ""
                                            : notification.getType().name()
                            )

                            .putData(
                                    "metadataJson",
                                    notification.getMetadataJson() != null
                                            ? notification.getMetadataJson()
                                            : "{}"
                            )

                            .putData(
                                    "actionsJson",
                                    notification.getActionsJson() != null
                                            ? notification.getActionsJson()
                                            : "[]"
                            )

                            .putData(
                                    "deepLink",
                                    notification.getDeepLink() != null
                                            ? notification.getDeepLink()
                                            : ""
                            );

            builder.addAllTokens(deviceTokens);

            MulticastMessage message =
                    builder.build();

            var response =
                    firebaseMessaging.sendMulticast(message);

            log.info(
                    "Multicast notification sent successfully. " +
                    "Success={}, Failure={}",
                    response.getSuccessCount(),
                    response.getFailureCount()
            );

            List<SendResponse> responses =
                    response.getResponses();

            for (int i = 0; i < responses.size(); i++) {

                SendResponse sendResponse =
                        responses.get(i);

                if (!sendResponse.isSuccessful()) {

                    String failedToken =
                            deviceTokens.get(i);

                    log.warn(
                            "Failed notification delivery. Token={}",
                            failedToken
                    );

                    deviceTokenService
                            .deactivateDeviceTokenByString(
                                    failedToken
                            );
                }
            }

        } catch (Exception e) {

            log.error(
                    "Error sending multicast notification",
                    e
            );
        }
    }

    @Override
    public CompletableFuture<Boolean> sendNotificationWithRetry(
            String userId,
            NotificationResponse notification,
            int maxRetries) {

        return CompletableFuture.supplyAsync(() -> {

            for (int i = 0; i < maxRetries; i++) {

                try {

                    var result =
                            sendPushNotificationToUser(
                                    userId,
                                    notification
                            ).get();

                    if (result) {
                        return true;
                    }

                } catch (Exception e) {

                    log.warn(
                            "Retry attempt {} failed for user: {}",
                            i + 1,
                            userId,
                            e
                    );

                    if (i < maxRetries - 1) {

                        try {

                            Thread.sleep(
                                    1000L * (long) Math.pow(2, i)
                            );

                        } catch (InterruptedException ex) {

                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }

            log.error(
                    "Failed to send notification to user: {} " +
                    "after {} retries",
                    userId,
                    maxRetries
            );

            return false;
        });
    }
}