package com.whoami.launch.service;

public interface AuditService {

    void log(
            String adminId,
            String action,
            String targetId,
            String ipAddress
    );
}