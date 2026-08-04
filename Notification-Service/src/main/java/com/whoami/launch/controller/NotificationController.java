package com.whoami.launch.controller;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.NotificationRequest;
import com.whoami.launch.dto.NotificationResponse;
import com.whoami.launch.dto.UnreadCountResponse;
import com.whoami.launch.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Notification Management
 */
@Slf4j
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Notification management endpoints")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * Create a new notification (Internal API for other services)
     */
    @PostMapping("/internal-api/notifications")
    @Operation(summary = "Create a new notification", description = "Internal API - Create notification for a user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Notification created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(
            @Valid @RequestBody NotificationRequest request) {
        log.info("Creating notification via internal API");
        NotificationResponse response = notificationService.createNotification(request);
        return new ResponseEntity<>(ApiResponse.created(response), HttpStatus.CREATED);
    }

    /**
     * Get paginated notifications for a user
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user notifications", description = "Get paginated notifications for a user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notifications retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getUserNotifications(
            @Parameter(description = "User ID") @PathVariable String userId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        log.info("Fetching notifications for user: {}", userId);
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationResponse> notifications = notificationService.getNotifications(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    /**
     * Get unread notifications for a user
     */
    @GetMapping("/unread/{userId}")
    @Operation(summary = "Get unread notifications", description = "Get unread notifications for a user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Unread notifications retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getUnreadNotifications(
            @Parameter(description = "User ID") @PathVariable String userId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        log.info("Fetching unread notifications for user: {}", userId);
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationResponse> notifications = notificationService.getUnreadNotifications(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    /**
     * Get unread count for a user
     */
    @GetMapping("/unread-count/{userId}")
    @Operation(summary = "Get unread notification count", description = "Get unread and total notification count for a user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    })
    public ResponseEntity<ApiResponse<UnreadCountResponse>> getUnreadCount(
            @Parameter(description = "User ID") @PathVariable String userId) {
        log.info("Fetching unread count for user: {}", userId);
        UnreadCountResponse response = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Get notifications with optional type filter
     */
    @GetMapping("/user/{userId}/type")
    @Operation(summary = "Get notifications by type", description = "Get notifications of a specific type for a user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotificationsByType(
            @Parameter(description = "User ID") @PathVariable String userId,
            @Parameter(description = "Notification type (ORDER, CHAT, SHOP, etc.)") @RequestParam String type,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        log.info("Fetching notifications for user: {} with type: {}", userId, type);
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationResponse> notifications = notificationService.getNotificationsByType(userId, type, pageable);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    /**
     * Get notification by ID
     */
    @GetMapping("/{notificationId}")
    @Operation(summary = "Get notification by ID", description = "Retrieve a specific notification by its ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notification retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<ApiResponse<NotificationResponse>> getNotificationById(
            @Parameter(description = "Notification ID") @PathVariable String notificationId) {
        log.info("Fetching notification: {}", notificationId);
        NotificationResponse notification = notificationService.getNotificationById(notificationId);
        return ResponseEntity.ok(ApiResponse.success(notification));
    }

    /**
     * Mark notification as read
     */
    @PutMapping("/read/{notificationId}")
    @Operation(summary = "Mark notification as read", description = "Mark a notification as read")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notification marked as read"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @Parameter(description = "Notification ID") @PathVariable String notificationId) {
        log.info("Marking notification as read: {}", notificationId);
        NotificationResponse response = notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.success(response, "Notification marked as read"));
    }

    /**
     * Mark all notifications as read for a user
     */
    @PutMapping("/read-all/{userId}")
    @Operation(summary = "Mark all notifications as read", description = "Mark all notifications as read for a user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "All notifications marked as read")
    })
    public ResponseEntity<ApiResponse<Object>> markAllAsRead(
            @Parameter(description = "User ID") @PathVariable String userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "All notifications marked as read"));
    }

    /**
     * Delete a notification
     */
    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Delete a notification", description = "Delete a notification (soft delete)")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Notification deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Notification not found")
    })
    public ResponseEntity<ApiResponse<Object>> deleteNotification(
            @Parameter(description = "Notification ID") @PathVariable String notificationId) {
        log.info("Deleting notification: {}", notificationId);
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok(ApiResponse.success(null, "Notification deleted successfully"));
    }

    /**
     * Check if user has unread notifications
     */
    @GetMapping("/has-unread/{userId}")
    @Operation(summary = "Check for unread notifications", description = "Check if user has any unread notifications")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Check completed successfully")
    })
    public ResponseEntity<ApiResponse<Object>> hasUnreadNotifications(
            @Parameter(description = "User ID") @PathVariable String userId) {
        log.info("Checking for unread notifications for user: {}", userId);
        boolean hasUnread = notificationService.hasUnreadNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success(hasUnread, "Has unread: " + hasUnread));
    }
}
