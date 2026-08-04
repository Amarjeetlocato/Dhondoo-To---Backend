package com.whoami.launch.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for unread count response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Unread Count Response DTO")
public class UnreadCountResponse {

    @Schema(description = "User ID", example = "user-123")
    private String userId;

    @Schema(description = "Count of unread notifications", example = "5")
    private Long unreadCount;

    @Schema(description = "Total notification count", example = "20")
    private Long totalCount;
}
