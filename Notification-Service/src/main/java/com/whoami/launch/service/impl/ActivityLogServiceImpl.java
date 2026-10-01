package com.whoami.launch.service.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.whoami.launch.dto.ActivityLogResponse;
import com.whoami.launch.entity.ActivityLog;
import com.whoami.launch.repository.ActivityLogRepository;
import com.whoami.launch.service.ActivityLogService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public ActivityLogServiceImpl(
            ActivityLogRepository activityLogRepository) {

        this.activityLogRepository = activityLogRepository;
    }

    @Override
    @Transactional
    public ActivityLogResponse logActivity(
            String userId,
            String title,
            String description) {

        log.info(
                "Logging activity for user: {} - Title: {}",
                userId,
                title
        );

        String activityId =
                "ACTIVITY_" + UUID.randomUUID();

        ActivityLog activityLog =
                ActivityLog.builder()
                        .activityId(activityId)
                        .userId(userId)
                        .title(title)
                        .description(description)
                        .build();

        ActivityLog savedActivity =
                activityLogRepository.save(activityLog);

        return mapToResponse(savedActivity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ActivityLogResponse> getActivityLogs(
            String userId,
            Pageable pageable) {

        log.info(
                "Fetching activity logs for user: {}",
                userId
        );

        return activityLogRepository
                .findByUserIdOrderByCreatedAtDesc(
                        userId,
                        pageable
                )
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ActivityLogResponse getActivityLogById(
            String activityId) {

        log.info(
                "Fetching activity log: {}",
                activityId
        );

        ActivityLog activity =
                activityLogRepository
                        .findByActivityId(activityId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Activity log not found: "
                                                + activityId
                                )
                        );

        return mapToResponse(activity);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getActivityCount(
            String userId) {

        return activityLogRepository
                .countByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteActivityLog(
            String activityId) {

        log.info(
                "Deleting activity log: {}",
                activityId
        );

        ActivityLog activity =
                activityLogRepository
                        .findByActivityId(activityId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Activity log not found: "
                                                + activityId
                                )
                        );

        activityLogRepository.delete(activity);
    }

    private ActivityLogResponse mapToResponse(
            ActivityLog activity) {

        return ActivityLogResponse.builder()
                .activityId(activity.getActivityId())
                .userId(activity.getUserId())
                .title(activity.getTitle())
                .description(activity.getDescription())
                .createdAt(activity.getCreatedAt())
                .build();
    }
}