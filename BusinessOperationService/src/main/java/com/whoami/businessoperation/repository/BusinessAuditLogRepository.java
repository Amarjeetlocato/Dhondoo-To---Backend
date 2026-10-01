package com.whoami.businessoperation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whoami.businessoperation.domain.entity.BusinessAuditLog;
import com.whoami.businessoperation.domain.enums.AuditAction;

public interface BusinessAuditLogRepository
        extends JpaRepository<BusinessAuditLog, Long> {

    List<BusinessAuditLog> findByBusinessIdOrderByCreatedAtDesc(
    		String businessId
    );

    List<BusinessAuditLog> findByApplicationIdOrderByCreatedAtDesc(
    		String applicationId
    );

    List<BusinessAuditLog> findByBusinessIdAndActionOrderByCreatedAtDesc(
    		String businessId,
            AuditAction action
    );
}