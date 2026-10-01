package com.whoami.businessoperation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whoami.businessoperation.domain.entity.BusinessVerification;
import com.whoami.businessoperation.domain.enums.VerificationStatus;

public interface BusinessVerificationRepository
        extends JpaRepository<BusinessVerification, Long> {

    Optional<BusinessVerification> findByBusinessId(String businessId);

    Optional<BusinessVerification> findByApplicationId(String applicationId);

    List<BusinessVerification> findByVerificationStatus(
            VerificationStatus verificationStatus
    );

    boolean existsByBusinessId(String businessId);
}