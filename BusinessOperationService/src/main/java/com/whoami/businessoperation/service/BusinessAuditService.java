package com.whoami.businessoperation.service;

import com.whoami.businessoperation.domain.enums.AuditAction;
import com.whoami.businessoperation.domain.entity.BusinessAuditLog;

import java.util.List;
import java.util.UUID;

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