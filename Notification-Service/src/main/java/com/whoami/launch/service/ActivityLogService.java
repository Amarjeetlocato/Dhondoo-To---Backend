package com.whoami.launch.service;

import com.whoami.launch.dto.ActivityLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ActivityLogService {

    ActivityLogResponse logActivity(
            String userId,
            String title,
            String description
    );

    Page<ActivityLogResponse> getActivityLogs(
            String userId,
            Pageable pageable
    );

    ActivityLogResponse getActivityLogById(
            String activityId
    );

    Long getActivityCount(
            String userId
    );

    void deleteActivityLog(
            String activityId
    );
}