package com.whoami.launch.service;

import com.whoami.launch.dto.ActivityLogResponse;
import com.whoami.launch.entity.ActivityLog;
import com.whoami.launch.repository.ActivityLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for managing activity logs
 */
@Slf4j
@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogService(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    /**
     * Create a new activity log entry
     */
    @Transactional
    public ActivityLogResponse logActivity(String userId, String title, String description) {
        log.info("Logging activity for user: {} - Title: {}", userId, title);

        String activityId = UUID.randomUUID().toString();

        ActivityLog activityLog = ActivityLog.builder()
                .activityId(activityId)
                .userId(userId)
                .title(title)
                .description(description)
                .build();

        ActivityLog savedActivity = activityLogRepository.save(activityLog);
        return mapToResponse(savedActivity);
    }

    /**
     * Get paginated activity logs for a user
     */
    public Page<ActivityLogResponse> getActivityLogs(String userId, Pageable pageable) {
        log.info("Fetching activity logs for user: {}", userId);
        return activityLogRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Get activity log by ID
     */
    public ActivityLogResponse getActivityLogById(String activityId) {
        log.info("Fetching activity log: {}", activityId);

        ActivityLog activity = activityLogRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity log not found: " + activityId));

        return mapToResponse(activity);
    }

    /**
     * Get total activity count for a user
     */
    public Long getActivityCount(String userId) {
        return activityLogRepository.countByUserId(userId);
    }

    /**
     * Delete activity log
     */
    @Transactional
    public void deleteActivityLog(String activityId) {
        log.info("Deleting activity log: {}", activityId);
        activityLogRepository.deleteById(activityId);
    }

    /**
     * Map entity to response DTO
     */
    private ActivityLogResponse mapToResponse(ActivityLog activity) {
        return ActivityLogResponse.builder()
                .activityId(activity.getActivityId())
                .userId(activity.getUserId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .createdAt(activity.getCreatedAt())
                .build();
    }
}
