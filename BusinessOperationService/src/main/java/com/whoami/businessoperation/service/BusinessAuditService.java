package com.whoami.businessoperation.service;

import java.util.List;

import com.whoami.businessoperation.domain.entity.BusinessAuditLog;
import com.whoami.businessoperation.domain.enums.AuditAction;

public interface BusinessAuditService {

    void log(
            String businessId,
            String applicationId,
            AuditAction action,
            String performedBy,
            String performedByRole,
            String description,
            String ipAddress,
            String metadata
    );

    List<BusinessAuditLog> getBusinessAuditLogs(
            String businessId
    );

    List<BusinessAuditLog> getApplicationAuditLogs(
            String applicationId
    );
}

