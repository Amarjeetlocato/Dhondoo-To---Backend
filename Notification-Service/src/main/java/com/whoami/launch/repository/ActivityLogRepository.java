package com.whoami.launch.repository;

import com.whoami.launch.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for ActivityLog entity
 */
@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, String> {

    /**
     * Get activity logs for a user, ordered by latest first
     */
    Page<ActivityLog> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    /**
     * Count activity logs for a user
     */
    Long countByUserId(String userId);
}
