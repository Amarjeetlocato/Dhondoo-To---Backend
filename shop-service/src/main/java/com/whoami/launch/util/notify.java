package com.whoami.launch.util;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.whoami.launch.entity.Shop;
import com.whoami.launch.entity.ShopFollower;
import com.whoami.launch.enums.NotificationType;
import com.whoami.launch.repository.ShopFollowerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class notify {

    private final ShopFollowerRepository followerRepository;
    private final NotificationProducer notificationProducer;

    @Async
    public void notifyFollowers(
            Shop shop,
            String title,
            String message,
            String targetId,
            String targetType,
            NotificationType notificationType) {

        List<ShopFollower> followers =
                followerRepository.findByShopId(shop.getShopId());

        for (ShopFollower follower : followers) {

            if (follower.getUserId().equals(shop.getUserId())) {
                continue;
            }

            sendNotification(
                    follower.getUserId(),
                    title,
                    message,
                    targetId,
                    targetType,
                    notificationType
            );
        }
    }

    private void sendNotification(
            String userId,
            String title,
            String message,
            String targetId,
            String targetType,
            NotificationType notificationType) {

        try {

            NotificationEvent event = NotificationEvent.builder()
                    .userId(userId)
                    .title(title)
                    .message(message)
                    .imageUrl(null)
                    .targetId(targetId)
                    .targetType(targetType)
                    .type(notificationType)
                    .sendPush(true)
                    .metadataJson(null)
                    .actionsJson(null)
                    .deepLink(null)
                    .build();

            notificationProducer.sendNotification(event);

        } catch (Exception e) {

            System.err.println("Failed to send notification: " + e.getMessage());
            e.printStackTrace();
        }
    }
}