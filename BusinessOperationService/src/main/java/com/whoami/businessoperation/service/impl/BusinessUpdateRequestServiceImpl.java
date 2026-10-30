package com.whoami.businessoperation.service.impl;

import com.whoami.businessoperation.domain.entity.BusinessApplication;
import com.whoami.businessoperation.domain.entity.BusinessUpdateRequest;
import com.whoami.businessoperation.dto.request.SubmitBusinessUpdateRequest;
import com.whoami.businessoperation.repository.BusinessApplicationRepository;
import com.whoami.businessoperation.repository.BusinessUpdateRequestRepository;
import com.whoami.businessoperation.service.BusinessUpdateRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BusinessUpdateRequestServiceImpl implements BusinessUpdateRequestService {

    private final BusinessUpdateRequestRepository repository;
    private final BusinessApplicationRepository applicationRepository;

    @Override
    public BusinessUpdateRequest submitRequest(SubmitBusinessUpdateRequest request) {
        log.info("Submitting business update request for businessId={}", request.getBusinessId());

        BusinessUpdateRequest entity = BusinessUpdateRequest.builder()
                .businessId(request.getBusinessId())
                .requestedBusinessName(request.getRequestedBusinessName())
                .requestedPhone(request.getRequestedPhone())
                .requestedEmail(request.getRequestedEmail())
                .requestedAddress(request.getRequestedAddress())
                .requestedLatitude(request.getRequestedLatitude())
                .requestedLongitude(request.getRequestedLongitude())
                .reason(request.getReason())
                .status("PENDING_APPROVAL")
                .build();

        return repository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusinessUpdateRequest> getRequestsByBusiness(String businessId) {
        return repository.findByBusinessIdOrderByCreatedAtDesc(businessId);
    }

    @Override
    public BusinessUpdateRequest approveRequest(String requestId, String reviewedBy) {
        log.info("Approving business update request: requestId={}, reviewedBy={}", requestId, reviewedBy);

        BusinessUpdateRequest request = repository.findByRequestId(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Business update request not found: " + requestId));

        request.setStatus("APPROVED");
        request.setReviewedBy(reviewedBy);
        request.setReviewedAt(LocalDateTime.now());

        BusinessUpdateRequest saved = repository.save(request);

        // Update application entity if present
        applicationRepository.findByBusinessId(request.getBusinessId()).ifPresent(app -> {
            if (request.getRequestedBusinessName() != null && !request.getRequestedBusinessName().isBlank()) {
                app.setBusinessName(request.getRequestedBusinessName());
            }
            if (request.getRequestedPhone() != null && !request.getRequestedPhone().isBlank()) {
                app.setPhone(request.getRequestedPhone());
            }
            if (request.getRequestedEmail() != null && !request.getRequestedEmail().isBlank()) {
                app.setEmail(request.getRequestedEmail());
            }
            if (request.getRequestedAddress() != null && !request.getRequestedAddress().isBlank()) {
                app.setAddress(request.getRequestedAddress());
            }
            if (request.getRequestedLatitude() != null) {
                app.setLatitude(request.getRequestedLatitude());
            }
            if (request.getRequestedLongitude() != null) {
                app.setLongitude(request.getRequestedLongitude());
            }
            applicationRepository.save(app);
        });

        return saved;
    }

    @Override
    public BusinessUpdateRequest rejectRequest(String requestId, String reason, String reviewedBy) {
        log.info("Rejecting business update request: requestId={}, reason={}", requestId, reason);

        BusinessUpdateRequest request = repository.findByRequestId(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Business update request not found: " + requestId));

        request.setStatus("REJECTED");
        request.setReason(reason);
        request.setReviewedBy(reviewedBy);
        request.setReviewedAt(LocalDateTime.now());

        return repository.save(request);
    }
}
