package com.whoami.businessoperation.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.whoami.businessoperation.domain.entity.BusinessAuditLog;
import com.whoami.businessoperation.domain.enums.AuditAction;
import com.whoami.businessoperation.repository.BusinessAuditLogRepository;
import com.whoami.businessoperation.service.BusinessAuditService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BusinessAuditServiceImpl implements BusinessAuditService {

    private final BusinessAuditLogRepository businessAuditLogRepository;

    @Override
    public void log(
            UUID businessId,
            UUID applicationId,
            AuditAction action,
            UUID performedBy,
            String performedByRole,
            String description,
            String ipAddress,
            String metadata) {

        BusinessAuditLog auditLog = new BusinessAuditLog();

        auditLog.setBusinessId(businessId);
        auditLog.setApplicationId(applicationId);
        auditLog.setAction(action);
        auditLog.setPerformedBy(performedBy);
        auditLog.setPerformedByRole(performedByRole);
        auditLog.setDescription(description);
        auditLog.setIpAddress(ipAddress);
        auditLog.setMetadata(metadata);

        businessAuditLogRepository.save(auditLog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusinessAuditLog> getBusinessAuditLogs(
            UUID businessId) {

        return businessAuditLogRepository
                .findByBusinessIdOrderByCreatedAtDesc(businessId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusinessAuditLog> getApplicationAuditLogs(
            UUID applicationId) {

        return businessAuditLogRepository
                .findByApplicationIdOrderByCreatedAtDesc(applicationId);
    }
}