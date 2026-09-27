package com.whoami.businessoperation.service;

import com.whoami.businessoperation.domain.enums.AuditAction;
import com.whoami.businessoperation.domain.entity.BusinessAuditLog;

import java.util.List;
import java.util.UUID;

public interface BusinessAuditService {

    void log(
            UUID businessId,
            UUID applicationId,
            AuditAction action,
            UUID performedBy,
            String performedByRole,
            String description,
            String ipAddress,
            String metadata
    );

    List<BusinessAuditLog> getBusinessAuditLogs(
            UUID businessId
    );

    List<BusinessAuditLog> getApplicationAuditLogs(
            UUID applicationId
    );
}