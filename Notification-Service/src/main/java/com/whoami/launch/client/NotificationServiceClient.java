//package com.whoami.launch.client;
//
//import com.whoami.launch.dto.ApiResponse;
//import com.whoami.launch.dto.NotificationRequest;
//import com.whoami.launch.dto.NotificationResponse;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.*;
//
///**
// * Feign client for Notification Service
// * Used by other microservices to create and manage notifications
// */
//@FeignClient(name = "notification-service", url = "${notification-service.url:http://localhost:8087}")
//public interface NotificationServiceClient {
//
//    /**
//     * Create a notification via internal API
//     * This endpoint is designed for inter-service communication
//     */
//    @PostMapping("/api/notifications/internal-api/notifications")
//    ApiResponse<NotificationResponse> createNotification(
//            @RequestBody NotificationRequest request);
//
//    /**
//     * Get notifications for a user
//     */
//    @GetMapping("/api/notifications/user/{userId}")
//    ApiResponse<Object> getUserNotifications(
//            @PathVariable("userId") String userId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size);
//
//    /**
//     * Get unread notification count
//     */
//    @GetMapping("/api/notifications/unread-count/{userId}")
//    ApiResponse<Object> getUnreadCount(
//            @PathVariable("userId") String userId);
//
//    /**
//     * Mark notification as read
//     */
//    @PutMapping("/api/notifications/read/{notificationId}")
//    ApiResponse<NotificationResponse> markAsRead(
//            @PathVariable("notificationId") String notificationId);
//
//    /**
//     * Mark all notifications as read
//     */
//    @PutMapping("/api/notifications/read-all/{userId}")
//    ApiResponse<Object> markAllAsRead(
//            @PathVariable("userId") String userId);
//
//    /**
//     * Delete a notification
//     */
//    @DeleteMapping("/api/notifications/{notificationId}")
//    ApiResponse<Object> deleteNotification(
//            @PathVariable("notificationId") String notificationId);
//}
