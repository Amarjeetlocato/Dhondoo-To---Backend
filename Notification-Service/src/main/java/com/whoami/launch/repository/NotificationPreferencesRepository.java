package com.whoami.launch.repository;

import com.whoami.launch.entity.NotificationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for NotificationPreferences entity
 */
@Repository
public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferences, Long> {

    /**
     * Find preferences by user ID
     */
    Optional<NotificationPreferences> findByUserId(String userId);
}
