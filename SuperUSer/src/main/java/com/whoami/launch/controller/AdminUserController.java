package com.whoami.launch.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.UserResponseDto;
import com.whoami.launch.service.KafkaEventPublisherService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final KafkaEventPublisherService eventPublisherService;

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = new ArrayList<>();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable String userId) {
        UserResponseDto user = new UserResponseDto();
        user.setUserId(userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{userId}/block")
    public ResponseEntity<ApiResponse<String>> blockUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "Blocked by admin") String reason
    ) {
        eventPublisherService.sendNotification(
                userId,
                "Account Status Warning ⚠️",
                "Your account access has been restricted: " + reason,
                userId,
                "USER",
                "SYSTEM",
                "/account/login"
        );
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User blocked successfully", userId)
        );
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String userId) {
        eventPublisherService.sendNotification(
                userId,
                "Account Terminated",
                "Your account has been deleted by system admin.",
                userId,
                "USER",
                "SYSTEM",
                "/login"
        );
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User deleted successfully", userId)
        );
    }
}
