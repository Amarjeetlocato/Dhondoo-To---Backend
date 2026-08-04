package com.whoami.launch.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for notification preferences request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Notification Preferences Request DTO")
public class NotificationPreferencesRequest {

    @Schema(description = "Enable order notifications", example = "true")
    private Boolean orderNotification;

    @Schema(description = "Enable chat notifications", example = "true")
    private Boolean chatNotification;

    @Schema(description = "Enable promotion notifications", example = "true")
    private Boolean promotionNotification;

    @Schema(description = "Enable reel notifications", example = "true")
    private Boolean reelNotification;

    @Schema(description = "Enable product notifications", example = "true")
    private Boolean productNotification;

    @Schema(description = "Enable shop notifications", example = "true")
    private Boolean shopNotification;

    @Schema(description = "Enable service notifications", example = "true")
    private Boolean serviceNotification;

    @Schema(description = "Enable admin notifications", example = "true")
    private Boolean adminNotification;

    @Schema(description = "Enable follow notifications", example = "true")
    private Boolean followNotification;
}
