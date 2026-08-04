package com.whoami.launch.dto;

import java.util.List;

import com.whoami.launch.enums.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new notification
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Notification Request DTO")
public class NotificationRequest  {

    @NotBlank(message = "User ID is required")
    @Schema(description = "User ID", example = "user-123")
    private String userId;

    @NotBlank(message = "Title is required")
    @Schema(description = "Notification title", example = "Order Confirmed")
    private String title;

    @NotBlank(message = "Message is required")
    @Schema(description = "Notification message", example = "Your order has been confirmed")
    private String message;

    @Schema(description = "Image URL", example = "https://example.com/image.jpg")
    private String imageUrl;

    @Schema(description = "Target ID (e.g., order ID, product ID)", example = "order-456")
    private String targetId;

    @Schema(description = "Target type (e.g., ORDER, PRODUCT)", example = "ORDER")
    private String targetType;

    @NotNull(message = "Notification type is required")
    @Schema(description = "Notification type", example = "ORDER")
    private NotificationType type;

    @Schema(description = "Whether to send FCM push notification", example = "true")
    @Builder.Default
    private Boolean sendPush = true;
    
    private String metadataJson;
    private String actionsJson;
    private String deepLink;
}
