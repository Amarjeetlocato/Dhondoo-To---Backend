package com.whoami.launch.dto;

import com.whoami.launch.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for notification response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Notification Response DTO")
public class NotificationResponse {

    @Schema(description = "Notification ID", example = "notif-123")
    private String notificationId;

    @Schema(description = "User ID", example = "user-123")
    private String userId;

    @Schema(description = "Notification title", example = "Order Confirmed")
    private String title;

    @Schema(description = "Notification message", example = "Your order has been confirmed")
    private String message;

    @Schema(description = "Image URL", example = "https://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "Target ID", example = "order-456")
    private String targetId;

    @Schema(description = "Target type", example = "ORDER")
    private String targetType;

    @Schema(description = "Notification type", example = "ORDER")
    private NotificationType type;

    @Schema(description = "Whether notification is read", example = "false")
    private Boolean isRead;

    @Schema(description = "Whether notification is deleted", example = "false")
    private Boolean isDeleted;

    @Schema(description = "Creation timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
    
    private String metadataJson;
    private String actionsJson;
    private String deepLink;
}
