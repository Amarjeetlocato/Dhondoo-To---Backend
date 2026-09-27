package com.whoami.businessoperation.repository;

import com.whoami.businessoperation.domain.entity.BusinessCapability;
import com.whoami.businessoperation.domain.enums.CapabilityStatus;
import com.whoami.businessoperation.domain.enums.CapabilityType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessCapabilityRepository
        extends JpaRepository<BusinessCapability, UUID> {

    List<BusinessCapability> findByBusinessId(UUID businessId);

    Optional<BusinessCapability> findByBusinessIdAndCapabilityType(
            UUID businessId,
            CapabilityType capabilityType
    );

    List<BusinessCapability> findByBusinessIdAndCapabilityStatus(
            UUID businessId,
            CapabilityStatus capabilityStatus
    );

    boolean existsByBusinessIdAndCapabilityType(
            UUID businessId,
            CapabilityType capabilityType
    );
}