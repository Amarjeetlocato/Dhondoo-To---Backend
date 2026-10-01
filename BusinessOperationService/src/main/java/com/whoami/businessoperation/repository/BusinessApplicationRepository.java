package com.whoami.businessoperation.repository;

import com.whoami.businessoperation.domain.entity.BusinessApplication;
import com.whoami.businessoperation.domain.enums.BusinessApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessApplicationRepository
        extends JpaRepository<BusinessApplication, Long> {

    Optional<BusinessApplication> findByApplicationId(
            String applicationId
    );

    Optional<BusinessApplication> findByBusinessId(
            String businessId
    );

    Optional<BusinessApplication> findByOwnerUserIdAndBusinessId(
            String ownerUserId,
            String businessId
    );

    List<BusinessApplication> findByOwnerUserId(
            String ownerUserId
    );

    List<BusinessApplication> findByApplicationStatus(
            BusinessApplicationStatus applicationStatus
    );

    boolean existsByBusinessId(
            String businessId
    );
}