package com.whoami.businessoperation.repository;

import com.whoami.businessoperation.domain.entity.BusinessVerificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BusinessVerificationHistoryRepository
        extends JpaRepository<BusinessVerificationHistory, UUID> {

    List<BusinessVerificationHistory> findByBusinessIdOrderByCreatedAtDesc(
            UUID businessId
    );

    List<BusinessVerificationHistory> findByApplicationIdOrderByCreatedAtDesc(
            UUID applicationId
    );

    List<BusinessVerificationHistory> findByVerificationIdOrderByCreatedAtDesc(
            UUID verificationId
    );
}