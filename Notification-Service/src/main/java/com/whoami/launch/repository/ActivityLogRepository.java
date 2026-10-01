package com.whoami.launch.repository;

import com.whoami.launch.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for ActivityLog entity
 */
@Repository
public interface ActivityLogRepository
        extends JpaRepository<ActivityLog, Long> {

    Page<ActivityLog> findByUserIdOrderByCreatedAtDesc(
            String userId,
            Pageable pageable
    );

    Long countByUserId(String userId);

    Optional<ActivityLog> findByActivityId(
            String activityId
    );
}