package com.whoami.businessoperation.repository;

import com.whoami.businessoperation.domain.entity.BusinessAuditLog;
import com.whoami.businessoperation.domain.enums.AuditAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BusinessAuditLogRepository
        extends JpaRepository<BusinessAuditLog, UUID> {

    List<BusinessAuditLog> findByBusinessIdOrderByCreatedAtDesc(
            UUID businessId
    );

    List<BusinessAuditLog> findByApplicationIdOrderByCreatedAtDesc(
            UUID applicationId
    );

    List<BusinessAuditLog> findByBusinessIdAndActionOrderByCreatedAtDesc(
            UUID businessId,
            AuditAction action
    );
}