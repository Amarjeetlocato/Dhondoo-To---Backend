package com.whoami.businessoperation.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.locato.constants.events.BusinessEvent;
import com.locato.constants.events.BusinessEventType;
import com.whoami.businessoperation.domain.entity.BusinessVerification;
import com.whoami.businessoperation.domain.entity.BusinessVerificationHistory;
import com.whoami.businessoperation.domain.enums.AuditAction;
import com.whoami.businessoperation.domain.enums.VerificationAction;
import com.whoami.businessoperation.domain.enums.VerificationStatus;
import com.whoami.businessoperation.dto.request.ReviewVerificationRequest;
import com.whoami.businessoperation.dto.request.SubmitVerificationRequest;
import com.whoami.businessoperation.dto.response.BusinessVerificationResponse;
import com.whoami.businessoperation.kafka.BusinessEventProducer;
import com.whoami.businessoperation.repository.BusinessVerificationHistoryRepository;
import com.whoami.businessoperation.repository.BusinessVerificationRepository;
import com.whoami.businessoperation.service.BusinessAuditService;
import com.whoami.businessoperation.service.BusinessVerificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BusinessVerificationServiceImpl
        implements BusinessVerificationService {

    private final BusinessVerificationRepository verificationRepository;
    private final BusinessVerificationHistoryRepository historyRepository;
    private final BusinessAuditService businessAuditService;
    private final BusinessEventProducer businessEventProducer;

    @Override
    public BusinessVerificationResponse createVerification(
            UUID businessId,
            UUID applicationId) {

        if (verificationRepository.existsByBusinessId(businessId)) {
            throw new IllegalStateException(
                    "Verification already exists for businessId: "
                            + businessId
            );
        }

        BusinessVerification verification =
                new BusinessVerification();

        verification.setBusinessId(businessId);
        verification.setApplicationId(applicationId);
        verification.setVerificationStatus(
                VerificationStatus.PENDING
        );
        verification.setStartedAt(LocalDateTime.now());

        BusinessVerification saved =
                verificationRepository.save(verification);

        saveHistory(
                saved,
                VerificationAction.STARTED,
                null,
                VerificationStatus.PENDING,
                VerificationStatus.PENDING,
                null,
                "Verification process created"
        );

        businessAuditService.log(
                businessId,
                applicationId,
                AuditAction.VERIFICATION_STARTED,
                null,
                "SYSTEM",
                "Business verification process created",
                null,
                null
        );

        publishEvent(
                BusinessEventType.VERIFICATION_STARTED,
                saved,
                null,
                "SYSTEM",
                "Business verification process created",
                null
        );

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BusinessVerificationResponse getVerification(
            UUID businessId) {

        BusinessVerification verification =
                getVerificationEntity(businessId);

        return mapToResponse(verification);
    }

    @Override
    public BusinessVerificationResponse submitVerification(
            SubmitVerificationRequest request) {

        BusinessVerification verification =
                getVerificationEntity(request.getBusinessId());

        VerificationStatus previousStatus =
                verification.getVerificationStatus();

        if (previousStatus != VerificationStatus.PENDING
                && previousStatus != VerificationStatus.REUPLOAD_REQUIRED) {

            throw new IllegalStateException(
                    "Verification cannot be submitted in current status: "
                            + previousStatus
            );
        }

        verification.setVerificationVideoUrl(
                request.getVerificationVideoUrl()
        );

        verification.setVerificationVideoPublicId(
                request.getVerificationVideoPublicId()
        );

        verification.setSubmittedAt(LocalDateTime.now());

        verification.setVerificationStatus(
                VerificationStatus.PENDING
        );

        BusinessVerification saved =
                verificationRepository.save(verification);

        saveHistory(
                saved,
                VerificationAction.VIDEO_SUBMITTED,
                null,
                previousStatus,
                VerificationStatus.PENDING,
                null,
                "Verification video submitted"
        );

        businessAuditService.log(
                saved.getBusinessId(),
                saved.getApplicationId(),
                AuditAction.VERIFICATION_SUBMITTED,
                null,
                "BUSINESS_OWNER",
                "Business verification submitted",
                null,
                null
        );

        publishEvent(
                BusinessEventType.VERIFICATION_SUBMITTED,
                saved,
                null,
                "BUSINESS_OWNER",
                "Business verification submitted",
                null
        );

        return mapToResponse(saved);
    }

    @Override
    public BusinessVerificationResponse reviewVerification(
            ReviewVerificationRequest request) {

        BusinessVerification verification =
                getVerificationEntity(request.getBusinessId());

        VerificationStatus previousStatus =
                verification.getVerificationStatus();

        if (previousStatus == VerificationStatus.APPROVED
                || previousStatus == VerificationStatus.REJECTED) {

            throw new IllegalStateException(
                    "Verification has already been finalized with status: "
                            + previousStatus
            );
        }

        VerificationStatus newStatus =
                request.getVerificationStatus();

        verification.setVerificationStatus(newStatus);
        verification.setReviewerComment(
                request.getReviewerComment()
        );
        verification.setReviewedBy(
                request.getReviewedBy()
        );
        verification.setReviewedAt(
                LocalDateTime.now()
        );

        if (newStatus == VerificationStatus.APPROVED) {
            verification.setApprovedAt(LocalDateTime.now());
            verification.setRejectedAt(null);
        }

        if (newStatus == VerificationStatus.REJECTED) {
            verification.setRejectedAt(LocalDateTime.now());
            verification.setApprovedAt(null);
        }

        if (newStatus == VerificationStatus.REUPLOAD_REQUIRED) {
            verification.setApprovedAt(null);
            verification.setRejectedAt(null);
        }

        BusinessVerification saved =
                verificationRepository.save(verification);

        VerificationAction verificationAction =
                resolveVerificationAction(newStatus);

        saveHistory(
                saved,
                verificationAction,
                request.getReviewedBy(),
                previousStatus,
                newStatus,
                request.getReviewerComment(),
                "Verification status updated"
        );

        AuditAction auditAction =
                resolveAuditAction(newStatus);

        businessAuditService.log(
                saved.getBusinessId(),
                saved.getApplicationId(),
                auditAction,
                request.getReviewedBy(),
                "ADMIN",
                request.getReviewerComment(),
                null,
                null
        );

        BusinessEventType eventType =
                resolveEventType(newStatus);

        if (eventType != null) {

            publishEvent(
                    eventType,
                    saved,
                    request.getReviewedBy(),
                    "ADMIN",
                    "Business verification status updated to "
                            + newStatus,
                    request.getReviewerComment()
            );
        }

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<?> getVerificationHistory(
            UUID businessId) {

        return historyRepository
                .findByBusinessIdOrderByCreatedAtDesc(businessId);
    }

    private BusinessVerification getVerificationEntity(
            UUID businessId) {

        return verificationRepository.findByBusinessId(businessId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Business verification not found for businessId: "
                                        + businessId
                        ));
    }

    private void saveHistory(
            BusinessVerification verification,
            VerificationAction action,
            UUID performedBy,
            VerificationStatus previousStatus,
            VerificationStatus newStatus,
            String comment,
            String description) {

        BusinessVerificationHistory history =
                new BusinessVerificationHistory();

        history.setBusinessId(
                verification.getBusinessId()
        );

        history.setApplicationId(
                verification.getApplicationId()
        );

        history.setVerificationId(
                verification.getId()
        );

        history.setAction(action);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setPerformedBy(performedBy);
        history.setComment(
                comment != null
                        ? comment
                        : description
        );

        historyRepository.save(history);
    }

    private VerificationAction resolveVerificationAction(
            VerificationStatus status) {

        return switch (status) {

            case APPROVED ->
                    VerificationAction.APPROVED;

            case REJECTED ->
                    VerificationAction.REJECTED;

            case REUPLOAD_REQUIRED ->
                    VerificationAction.REUPLOAD_REQUESTED;

            case EXPIRED ->
                    VerificationAction.EXPIRED;

            case UNDER_REVIEW ->
                    VerificationAction.STARTED;

            default ->
                    VerificationAction.VERIFICATION_SUBMITTED;
        };
    }

    private AuditAction resolveAuditAction(
            VerificationStatus status) {

        return switch (status) {

            case APPROVED ->
                    AuditAction.VERIFICATION_APPROVED;

            case REJECTED ->
                    AuditAction.VERIFICATION_REJECTED;

            case REUPLOAD_REQUIRED ->
                    AuditAction.REUPLOAD_REQUESTED;

            default ->
                    AuditAction.VERIFICATION_SUBMITTED;
        };
    }

    private BusinessEventType resolveEventType(
            VerificationStatus status) {

        return switch (status) {

            case APPROVED ->
                    BusinessEventType.VERIFICATION_APPROVED;

            case REJECTED ->
                    BusinessEventType.VERIFICATION_REJECTED;

            case REUPLOAD_REQUIRED ->
                    BusinessEventType.VERIFICATION_REUPLOAD_REQUIRED;

            default ->
                    null;
        };
    }

    private void publishEvent(
            BusinessEventType eventType,
            BusinessVerification verification,
            UUID performedBy,
            String performedByRole,
            String description,
            String reason) {

        BusinessEvent event =
                BusinessEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(eventType)
                        .businessId(
                                verification.getBusinessId()
                        )
                        .applicationId(
                                verification.getApplicationId()
                        )
                        .performedBy(performedBy)
                        .performedByRole(performedByRole)
                        .description(description)
                        .reason(reason)
                        .occurredAt(LocalDateTime.now())
                        .build();

        businessEventProducer.publishVerificationEvent(event);
    }

    private BusinessVerificationResponse mapToResponse(
            BusinessVerification verification) {

        BusinessVerificationResponse response =
                new BusinessVerificationResponse();

        response.setId(verification.getId());
        response.setBusinessId(verification.getBusinessId());
        response.setApplicationId(verification.getApplicationId());
        response.setVerificationStatus(
                verification.getVerificationStatus()
        );
        response.setVerificationVideoUrl(
                verification.getVerificationVideoUrl()
        );
        response.setVerificationVideoPublicId(
                verification.getVerificationVideoPublicId()
        );
        response.setReviewerComment(
                verification.getReviewerComment()
        );
        response.setReviewedBy(
                verification.getReviewedBy()
        );
        response.setStartedAt(
                verification.getStartedAt()
        );
        response.setSubmittedAt(
                verification.getSubmittedAt()
        );
        response.setReviewedAt(
                verification.getReviewedAt()
        );
        response.setApprovedAt(
                verification.getApprovedAt()
        );
        response.setRejectedAt(
                verification.getRejectedAt()
        );
        response.setExpiresAt(
                verification.getExpiresAt()
        );
        response.setCreatedAt(
                verification.getCreatedAt()
        );
        response.setUpdatedAt(
                verification.getUpdatedAt()
        );

        return response;
    }
}