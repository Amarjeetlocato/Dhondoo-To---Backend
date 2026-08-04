package com.whoami.launch.controller;

import com.whoami.launch.dto.ActivityLogResponse;
import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.service.ActivityLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Activity Log Management
 */
@Slf4j
@RestController
@RequestMapping("/api/activity-logs")
@Tag(name = "Activity Logs", description = "User activity logs endpoints")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    /**
     * Get activity logs for a user
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user activity logs", description = "Retrieve paginated activity logs for a user")
    public ResponseEntity<ApiResponse<Page<ActivityLogResponse>>> getActivityLogs(
            @Parameter(description = "User ID") @PathVariable String userId,
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        log.info("Fetching activity logs for user: {}", userId);
        Pageable pageable = PageRequest.of(page, size);
        Page<ActivityLogResponse> logs = activityLogService.getActivityLogs(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    /**
     * Get activity log by ID
     */
    @GetMapping("/{activityId}")
    @Operation(summary = "Get activity log by ID", description = "Retrieve a specific activity log by its ID")
    public ResponseEntity<ApiResponse<ActivityLogResponse>> getActivityLogById(
            @Parameter(description = "Activity ID") @PathVariable String activityId) {
        log.info("Fetching activity log: {}", activityId);
        ActivityLogResponse log = activityLogService.getActivityLogById(activityId);
        return ResponseEntity.ok(ApiResponse.success(log));
    }

    /**
     * Get activity count for a user
     */
    @GetMapping("/count/{userId}")
    @Operation(summary = "Get user activity count", description = "Get total activity count for a user")
    public ResponseEntity<ApiResponse<Object>> getActivityCount(
            @Parameter(description = "User ID") @PathVariable String userId) {
        log.info("Fetching activity count for user: {}", userId);
        Long count = activityLogService.getActivityCount(userId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    /**
     * Delete activity log
     */
    @DeleteMapping("/{activityId}")
    @Operation(summary = "Delete activity log", description = "Delete a specific activity log")
    public ResponseEntity<ApiResponse<Object>> deleteActivityLog(
            @Parameter(description = "Activity ID") @PathVariable String activityId) {
        log.info("Deleting activity log: {}", activityId);
        activityLogService.deleteActivityLog(activityId);
        return ResponseEntity.ok(ApiResponse.success(null, "Activity log deleted successfully"));
    }
}
