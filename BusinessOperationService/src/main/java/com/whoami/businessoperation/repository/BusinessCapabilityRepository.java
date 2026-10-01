package com.whoami.businessoperation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whoami.businessoperation.domain.entity.BusinessCapability;
import com.whoami.businessoperation.domain.enums.CapabilityStatus;
import com.whoami.businessoperation.domain.enums.CapabilityType;

public interface BusinessCapabilityRepository
        extends JpaRepository<BusinessCapability, Long> {

    List<BusinessCapability> findByBusinessId(String businessId);

    Optional<BusinessCapability> findByBusinessIdAndCapabilityType(
    		String businessId,
            CapabilityType capabilityType
    );

    List<BusinessCapability> findByBusinessIdAndCapabilityStatus(
    		String businessId,
            CapabilityStatus capabilityStatus
    );

    boolean existsByBusinessIdAndCapabilityType(
    		String businessId,
            CapabilityType capabilityType
    );
}