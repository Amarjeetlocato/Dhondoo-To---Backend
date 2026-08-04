package com.whoami.launch.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whoami.launch.entity.AppCrashLog;

@Repository
public interface AppCrashLogRepository extends JpaRepository<AppCrashLog, Long> {

}