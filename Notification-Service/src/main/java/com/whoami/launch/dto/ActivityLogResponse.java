package com.whoami.launch.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for activity log response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Activity Log Response DTO")
public class ActivityLogResponse {

    @Schema(description = "Activity ID", example = "activity-123")
    private String activityId;

    @Schema(description = "User ID", example = "user-123")
    private String userId;

    @Schema(description = "Activity title", example = "Order Placed")
    private String title;

    @Schema(description = "Activity description", example = "Order #ORD-123 was placed")
    private String description;

    @Schema(description = "Creation timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}
