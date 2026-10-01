package com.whoami.launch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whoami.launch.entity.NotificationPreferences;

/**
 * Repository for NotificationPreferences entity
 */
@Repository
public interface NotificationPreferencesRepository
        extends JpaRepository<NotificationPreferences, Long> {

    Optional<NotificationPreferences> findByUserId(String userId);
}