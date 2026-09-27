package com.whoami.businessoperation.repository;

import com.whoami.businessoperation.domain.entity.BusinessVerification;
import com.whoami.businessoperation.domain.enums.VerificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessVerificationRepository
        extends JpaRepository<BusinessVerification, UUID> {

    Optional<BusinessVerification> findByBusinessId(UUID businessId);

    Optional<BusinessVerification> findByApplicationId(UUID applicationId);

    List<BusinessVerification> findByVerificationStatus(
            VerificationStatus verificationStatus
    );

    boolean existsByBusinessId(UUID businessId);
}