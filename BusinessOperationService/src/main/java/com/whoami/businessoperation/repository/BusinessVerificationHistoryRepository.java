package com.whoami.businessoperation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whoami.businessoperation.domain.entity.BusinessVerificationHistory;

public interface BusinessVerificationHistoryRepository
        extends JpaRepository<BusinessVerificationHistory, Long> {

    List<BusinessVerificationHistory> findByBusinessIdOrderByCreatedAtDesc(
    		String businessId
    );

    List<BusinessVerificationHistory> findByApplicationIdOrderByCreatedAtDesc(
    		String applicationId
    );

    List<BusinessVerificationHistory> findByVerificationIdOrderByCreatedAtDesc(
    		String verificationId
    );
}