package com.whoami.businessoperation.repository;

import com.whoami.businessoperation.domain.entity.BusinessUpdateRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessUpdateRequestRepository extends JpaRepository<BusinessUpdateRequest, Long> {

    Optional<BusinessUpdateRequest> findByRequestId(String requestId);

    List<BusinessUpdateRequest> findByBusinessIdOrderByCreatedAtDesc(String businessId);
}
