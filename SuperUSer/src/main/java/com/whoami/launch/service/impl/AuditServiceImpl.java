package com.whoami.launch.service.impl;

import org.springframework.stereotype.Service;

import com.whoami.launch.entity.AdminAuditLog;
import com.whoami.launch.repository.AdminAuditLogRepository;
import com.whoami.launch.service.AuditService;

@Service
public class AuditServiceImpl
        implements AuditService {

    private final AdminAuditLogRepository repository;

    public AuditServiceImpl(
            AdminAuditLogRepository repository
    ) {
        this.repository = repository;
    }

    @Override
    public void log(
            String adminId,
            String action,
            String targetId,
            String ipAddress
    ) {

        AdminAuditLog log =
                AdminAuditLog.builder()
                        .adminId(adminId)
                        .action(action)
                        .targetId(targetId)
                        .ipAddress(ipAddress)
                        .build();

        repository.save(log);
    }
}