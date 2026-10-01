package com.whoami.launch.controller;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.NotificationRequest;
import com.whoami.launch.dto.NotificationResponse;
import com.whoami.launch.dto.UnreadCountResponse;
import com.whoami.launch.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@Tag(
        name = "Notifications",
        description = "Notification management endpoints"
)
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/internal-api/notifications")
    @Operation(
            summary = "Create a new notification",
            description = "Internal API - Create notification for a user"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Notification created successfully"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request"
            )
    })
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(
            @Valid @RequestBody NotificationRequest request) {

        NotificationResponse response =
                notificationService.createNotification(request);

        return new ResponseEntity<>(
                ApiResponse.created(response),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Get user notifications",
            description = "Get paginated notifications for a user"
    )
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>>
    getUserNotifications(
            @Parameter(description = "User ID")
            @PathVariable String userId,

            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<NotificationResponse> notifications =
                notificationService.getNotifications(
                        userId,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(notifications)
        );
    }

    @GetMapping("/unread/{userId}")
    @Operation(
            summary = "Get unread notifications",
            description = "Get unread notifications for a user"
    )
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>>
    getUnreadNotifications(
            @Parameter(description = "User ID")
            @PathVariable String userId,

            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<NotificationResponse> notifications =
                notificationService.getUnreadNotifications(
                        userId,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(notifications)
        );
    }

    @GetMapping("/unread-count/{userId}")
    @Operation(
            summary = "Get unread notification count",
            description = "Get unread and total notification count for a user"
    )
    public ResponseEntity<ApiResponse<UnreadCountResponse>>
    getUnreadCount(
            @Parameter(description = "User ID")
            @PathVariable String userId) {

        UnreadCountResponse response =
                notificationService.getUnreadCount(userId);

        return ResponseEntity.ok(
                ApiResponse.success(response)
        );
    }

    @GetMapping("/user/{userId}/type")
    @Operation(
            summary = "Get notifications by type",
            description = "Get notifications of a specific type for a user"
    )
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>>
    getNotificationsByType(
            @Parameter(description = "User ID")
            @PathVariable String userId,

            @Parameter(description = "Notification type")
            @RequestParam String type,

            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<NotificationResponse> notifications =
                notificationService.getNotificationsByType(
                        userId,
                        type,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(notifications)
        );
    }

    @GetMapping("/{notificationId}")
    @Operation(
            summary = "Get notification by ID",
            description = "Retrieve a specific notification by its public notification ID"
    )
    public ResponseEntity<ApiResponse<NotificationResponse>>
    getNotificationById(
            @Parameter(description = "Notification ID")
            @PathVariable String notificationId) {

        NotificationResponse notification =
                notificationService.getNotificationById(notificationId);

        return ResponseEntity.ok(
                ApiResponse.success(notification)
        );
    }

    @PutMapping("/read/{notificationId}")
    @Operation(
            summary = "Mark notification as read",
            description = "Mark a notification as read"
    )
    public ResponseEntity<ApiResponse<NotificationResponse>>
    markAsRead(
            @Parameter(description = "Notification ID")
            @PathVariable String notificationId) {

        NotificationResponse response =
                notificationService.markAsRead(notificationId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        response,
                        "Notification marked as read"
                )
        );
    }

    @PutMapping("/read-all/{userId}")
    @Operation(
            summary = "Mark all notifications as read",
            description = "Mark all notifications as read for a user"
    )
    public ResponseEntity<ApiResponse<Object>>
    markAllAsRead(
            @Parameter(description = "User ID")
            @PathVariable String userId) {

        notificationService.markAllAsRead(userId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "All notifications marked as read"
                )
        );
    }

    @DeleteMapping("/{notificationId}")
    @Operation(
            summary = "Delete a notification",
            description = "Soft delete a notification"
    )
    public ResponseEntity<ApiResponse<Object>>
    deleteNotification(
            @Parameter(description = "Notification ID")
            @PathVariable String notificationId) {

        notificationService.deleteNotification(notificationId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Notification deleted successfully"
                )
        );
    }

    @GetMapping("/has-unread/{userId}")
    @Operation(
            summary = "Check for unread notifications",
            description = "Check if user has any unread notifications"
    )
    public ResponseEntity<ApiResponse<Object>>
    hasUnreadNotifications(
            @Parameter(description = "User ID")
            @PathVariable String userId) {

        boolean hasUnread =
                notificationService.hasUnreadNotifications(userId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        hasUnread,
                        "Has unread: " + hasUnread
                )
        );
    }
}