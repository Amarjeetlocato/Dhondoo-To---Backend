package com.whoami.launch.repository;

import com.whoami.launch.entity.AppCrashLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppCrashLogRepository
        extends JpaRepository<AppCrashLog, Long> {
}