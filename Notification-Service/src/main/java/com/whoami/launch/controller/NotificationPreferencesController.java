package com.whoami.launch.controller;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.NotificationPreferencesRequest;
import com.whoami.launch.dto.NotificationPreferencesResponse;
import com.whoami.launch.service.NotificationPreferencesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/preferences")
@Tag(
        name = "Notification Preferences",
        description = "User notification preferences endpoints"
)
public class NotificationPreferencesController {

    private final NotificationPreferencesService preferencesService;

    public NotificationPreferencesController(
            NotificationPreferencesService preferencesService) {
        this.preferencesService = preferencesService;
    }

    @GetMapping("/{userId}")
    @Operation(
            summary = "Get user preferences",
            description = "Retrieve notification preferences for a user"
    )
    public ResponseEntity<ApiResponse<NotificationPreferencesResponse>>
    getPreferences(
            @Parameter(description = "User ID")
            @PathVariable String userId) {

        NotificationPreferencesResponse preferences =
                preferencesService.getPreferences(userId);

        return ResponseEntity.ok(
                ApiResponse.success(preferences)
        );
    }

    @PutMapping("/{userId}")
    @Operation(
            summary = "Update user preferences",
            description = "Update notification preferences for a user"
    )
    public ResponseEntity<ApiResponse<NotificationPreferencesResponse>>
    updatePreferences(
            @Parameter(description = "User ID")
            @PathVariable String userId,

            @Valid @RequestBody
            NotificationPreferencesRequest request) {

        NotificationPreferencesResponse preferences =
                preferencesService.updatePreferences(
                        userId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(preferences)
        );
    }

    @PostMapping("/reset/{userId}")
    @Operation(
            summary = "Reset preferences to default",
            description = "Reset notification preferences to default for a user"
    )
    public ResponseEntity<ApiResponse<NotificationPreferencesResponse>>
    resetPreferences(
            @Parameter(description = "User ID")
            @PathVariable String userId) {

        NotificationPreferencesResponse preferences =
                preferencesService.resetToDefault(userId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        preferences,
                        "Preferences reset to default"
                )
        );
    }

    @GetMapping("/{userId}/check/{notificationType}")
    @Operation(
            summary = "Check notification type status",
            description = "Check if a specific notification type is enabled for a user"
    )
    public ResponseEntity<ApiResponse<Object>>
    isNotificationTypeEnabled(
            @Parameter(description = "User ID")
            @PathVariable String userId,

            @Parameter(description = "Notification type")
            @PathVariable String notificationType) {

        boolean isEnabled =
                preferencesService.isNotificationTypeEnabled(
                        userId,
                        notificationType
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        isEnabled,
                        notificationType
                                + " is "
                                + (isEnabled ? "enabled" : "disabled")
                )
        );
    }
}
