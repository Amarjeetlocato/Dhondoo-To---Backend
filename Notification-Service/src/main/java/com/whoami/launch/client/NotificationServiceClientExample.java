//package com.whoami.launch.client;
//
//import com.whoami.launch.dto.ApiResponse;
//import com.whoami.launch.dto.NotificationRequest;
//import com.whoami.launch.dto.NotificationResponse;
//import com.whoami.launch.enums.NotificationType;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
///**
// * Example service showing how to use the Notification Service client
// * This can be imported in other microservices (Order Service, Chat Service, etc.)
// * 
// * Usage in other services:
// * 1. Add this project as a dependency in your service's pom.xml
// * 2. Enable Feign clients: @EnableFeignClients
// * 3. Inject NotificationServiceClient and use it
// */
//@Slf4j
//@Service
//public class NotificationServiceClientExample {
//
//    private final NotificationServiceClient notificationServiceClient;
//
//    public NotificationServiceClientExample(NotificationServiceClient notificationServiceClient) {
//        this.notificationServiceClient = notificationServiceClient;
//    }
//
//    /**
//     * Example: Send order confirmation notification
//     * Call this from OrderService when an order is placed
//     */
//    public void sendOrderNotification(String userId, String orderId, String orderDetails) {
//        try {
//            NotificationRequest request = NotificationRequest.builder()
//                    .userId(userId)
//                    .title("Order Confirmed")
//                    .message("Your order " + orderId + " has been confirmed")
//                    .targetId(orderId)
//                    .targetType("ORDER")
//                    .type(NotificationType.ORDER)
//                    .imageUrl(null)
//                    .sendPush(true)
//                    .build();
//
//            ApiResponse<NotificationResponse> response = notificationServiceClient.createNotification(request);
//            if (response.getSuccess()) {
//                log.info("Order notification sent successfully for order: {}", orderId);
//            } else {
//                log.error("Failed to send order notification: {}", response.getMessage());
//            }
//        } catch (Exception e) {
//            log.error("Error sending order notification for user: {}", userId, e);
//        }
//    }
//
//    /**
//     * Example: Send product recommendation notification
//     * Call this from ProductService
//     */
//    public void sendProductNotification(String userId, String productId, String productName) {
//        try {
//            NotificationRequest request = NotificationRequest.builder()
//                    .userId(userId)
//                    .title("New Product Available")
//                    .message("Check out " + productName + " - based on your preferences")
//                    .targetId(productId)
//                    .targetType("PRODUCT")
//                    .type(NotificationType.PRODUCT)
//                    .imageUrl(null)
//                    .sendPush(true)
//                    .build();
//
//            notificationServiceClient.createNotification(request);
//            log.info("Product notification sent for product: {}", productId);
//        } catch (Exception e) {
//            log.error("Error sending product notification", e);
//        }
//    }
//
//    /**
//     * Example: Send promotion notification
//     * Call this from MarketingService
//     */
//    public void sendPromotionNotification(String userId, String promotionId, String title, String message) {
//        try {
//            NotificationRequest request = NotificationRequest.builder()
//                    .userId(userId)
//                    .title(title)
//                    .message(message)
//                    .targetId(promotionId)
//                    .targetType("PROMOTION")
//                    .type(NotificationType.PROMOTION)
//                    .sendPush(true)
//                    .build();
//
//            notificationServiceClient.createNotification(request);
//            log.info("Promotion notification sent for promotion: {}", promotionId);
//        } catch (Exception e) {
//            log.error("Error sending promotion notification", e);
//        }
//    }
//
//    /**
//     * Example: Send chat notification
//     * Call this from ChatService when a new message arrives
//     */
//    public void sendChatNotification(String userId, String chatId, String senderName, String message) {
//        try {
//            NotificationRequest request = NotificationRequest.builder()
//                    .userId(userId)
//                    .title("New message from " + senderName)
//                    .message(message)
//                    .targetId(chatId)
//                    .targetType("CHAT")
//                    .type(NotificationType.CHAT)
//                    .sendPush(true)
//                    .build();
//
//            notificationServiceClient.createNotification(request);
//            log.info("Chat notification sent for chat: {}", chatId);
//        } catch (Exception e) {
//            log.error("Error sending chat notification", e);
//        }
//    }
//
//    /**
//     * Example: Send follow notification
//     * Call this from SocialService when a user is followed
//     */
//    public void sendFollowNotification(String userId, String followerId, String followerName) {
//        try {
//            NotificationRequest request = NotificationRequest.builder()
//                    .userId(userId)
//                    .title("New Follower")
//                    .message(followerName + " started following you")
//                    .targetId(followerId)
//                    .targetType("USER")
//                    .type(NotificationType.FOLLOW)
//                    .sendPush(true)
//                    .build();
//
//            notificationServiceClient.createNotification(request);
//            log.info("Follow notification sent for user: {}", userId);
//        } catch (Exception e) {
//            log.error("Error sending follow notification", e);
//        }
//    }
//
//    /**
//     * Example: Send shop notification
//     * Call this from ShopService
//     */
//    public void sendShopNotification(String userId, String shopId, String shopName, String message) {
//        try {
//            NotificationRequest request = NotificationRequest.builder()
//                    .userId(userId)
//                    .title("Update from " + shopName)
//                    .message(message)
//                    .targetId(shopId)
//                    .targetType("SHOP")
//                    .type(NotificationType.SHOP)
//                    .sendPush(true)
//                    .build();
//
//            notificationServiceClient.createNotification(request);
//            log.info("Shop notification sent for shop: {}", shopId);
//        } catch (Exception e) {
//            log.error("Error sending shop notification", e);
//        }
//    }
//
//    /**
//     * Example: Get unread count for a user
//     */
//    public Object getUnreadCount(String userId) {
//        try {
//            return notificationServiceClient.getUnreadCount(userId);
//        } catch (Exception e) {
//            log.error("Error fetching unread count for user: {}", userId, e);
//            return null;
//        }
//    }
//
//    /**
//     * Example: Mark notification as read
//     */
//    public void markNotificationAsRead(String notificationId) {
//        try {
//            notificationServiceClient.markAsRead(notificationId);
//            log.info("Notification marked as read: {}", notificationId);
//        } catch (Exception e) {
//            log.error("Error marking notification as read: {}", notificationId, e);
//        }
//    }
//}
