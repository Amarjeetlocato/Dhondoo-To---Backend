package com.whoami.businessoperation.repository;

import com.whoami.businessoperation.domain.entity.BusinessApplication;
import com.whoami.businessoperation.domain.enums.BusinessApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessApplicationRepository
        extends JpaRepository<BusinessApplication, UUID> {

    Optional<BusinessApplication> findByBusinessId(UUID businessId);

    Optional<BusinessApplication> findByOwnerUserIdAndBusinessId(
            UUID ownerUserId,
            UUID businessId
    );

    List<BusinessApplication> findByOwnerUserId(UUID ownerUserId);

    List<BusinessApplication> findByApplicationStatus(
            BusinessApplicationStatus applicationStatus
    );

    boolean existsByBusinessId(UUID businessId);
}