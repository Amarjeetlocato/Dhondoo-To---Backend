package com.whoami.launch.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for notification preferences response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Notification Preferences Response DTO")
public class NotificationPreferencesResponse {

    @Schema(description = "Preference ID", example = "1")
    private Long preferenceId;

    @Schema(description = "User ID", example = "user-123")
    private String userId;

    @Schema(description = "Order notification enabled", example = "true")
    private Boolean orderNotification;

    @Schema(description = "Chat notification enabled", example = "true")
    private Boolean chatNotification;

    @Schema(description = "Promotion notification enabled", example = "true")
    private Boolean promotionNotification;

    @Schema(description = "Reel notification enabled", example = "true")
    private Boolean reelNotification;

    @Schema(description = "Product notification enabled", example = "true")
    private Boolean productNotification;

    @Schema(description = "Shop notification enabled", example = "true")
    private Boolean shopNotification;

    @Schema(description = "Service notification enabled", example = "true")
    private Boolean serviceNotification;

    @Schema(description = "Admin notification enabled", example = "true")
    private Boolean adminNotification;

    @Schema(description = "Follow notification enabled", example = "true")
    private Boolean followNotification;

    @Schema(description = "Creation timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
}
