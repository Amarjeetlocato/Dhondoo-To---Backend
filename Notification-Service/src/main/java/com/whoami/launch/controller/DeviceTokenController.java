package com.whoami.launch.controller;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.entity.DeviceToken;
import com.whoami.launch.service.DeviceTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/device-tokens")
@Tag(
        name = "Device Tokens",
        description = "Firebase Cloud Messaging device token endpoints"
)
public class DeviceTokenController {

    private final DeviceTokenService deviceTokenService;

    @PostMapping("/register")
    @Operation(
            summary = "Register device token",
            description = "Register a Firebase Cloud Messaging device token for a user"
    )
    public ResponseEntity<ApiResponse<DeviceToken>>
    registerDeviceToken(
            @Parameter(description = "User ID")
            @RequestParam String userId,

            @Parameter(description = "FCM Device Token")
            @RequestParam String deviceToken,

            @Parameter(description = "Device Type (iOS, Android, Web)")
            @RequestParam(required = false) String deviceType) {

        log.info(
                "Registering device token for user: {}",
                userId);

        DeviceToken token =
                deviceTokenService.registerDeviceToken(
                        userId,
                        deviceToken,
                        deviceType);

        return new ResponseEntity<>(
                ApiResponse.created(token),
                HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Get user device tokens",
            description = "Retrieve all active device tokens for a user"
    )
    public ResponseEntity<ApiResponse<List<DeviceToken>>>
    getActiveDeviceTokens(
            @Parameter(description = "User ID")
            @PathVariable String userId) {

        log.info(
                "Fetching active device tokens for user: {}",
                userId);

        List<DeviceToken> tokens =
                deviceTokenService.getActiveDeviceTokens(
                        userId);

        return ResponseEntity.ok(
                ApiResponse.success(tokens));
    }

    @PutMapping("/deactivate/{deviceTokenId}")
    @Operation(
            summary = "Deactivate device token",
            description = "Deactivate a device token"
    )
    public ResponseEntity<ApiResponse<Void>>
    deactivateDeviceToken(
            @Parameter(description = "Device Token ID")
            @PathVariable String deviceTokenId) {

        log.info(
                "Deactivating device token: {}",
                deviceTokenId);

        deviceTokenService.deactivateDeviceToken(
                deviceTokenId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Device token deactivated successfully"));
    }

    @DeleteMapping("/remove")
    @Operation(
            summary = "Remove device token",
            description = "Remove a device token for a user"
    )
    public ResponseEntity<ApiResponse<Void>>
    removeDeviceToken(
            @Parameter(description = "User ID")
            @RequestParam String userId,

            @Parameter(description = "Device Token")
            @RequestParam String deviceToken) {

        log.info(
                "Removing device token for user: {}",
                userId);

        deviceTokenService.removeDeviceToken(
                userId,
                deviceToken);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Device token removed successfully"));
    }

    @GetMapping("/has-active/{userId}")
    @Operation(
            summary = "Check active tokens",
            description = "Check if user has any active device tokens"
    )
    public ResponseEntity<ApiResponse<Boolean>>
    hasActiveTokens(
            @Parameter(description = "User ID")
            @PathVariable String userId) {

        log.info(
                "Checking active tokens for user: {}",
                userId);

        boolean hasActive =
                deviceTokenService.hasActiveTokens(userId);

        return ResponseEntity.ok(
                ApiResponse.success(hasActive));
    }

    @DeleteMapping("/user/{userId}")
    @Operation(
            summary = "Delete all user tokens",
            description = "Delete all device tokens for a user"
    )
    public ResponseEntity<ApiResponse<Void>>
    deleteAllTokens(
            @Parameter(description = "User ID")
            @PathVariable String userId) {

        log.info(
                "Deleting all device tokens for user: {}",
                userId);

        deviceTokenService.deleteAllTokensForUser(
                userId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "All device tokens deleted"));
    }
}