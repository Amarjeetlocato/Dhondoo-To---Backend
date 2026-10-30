package com.whoami.businessoperation.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.whoami.businessoperation.domain.entity.BusinessVerification;
import com.whoami.businessoperation.domain.entity.BusinessVerificationHistory;
import com.whoami.businessoperation.domain.enums.AuditAction;
import com.whoami.businessoperation.domain.enums.VerificationAction;
import com.whoami.businessoperation.domain.enums.VerificationStatus;
import com.whoami.businessoperation.dto.request.ReviewVerificationRequest;
import com.whoami.businessoperation.dto.request.SubmitVerificationRequest;
import com.whoami.businessoperation.dto.response.BusinessVerificationResponse;
import com.whoami.businessoperation.kafka.BusinessEventProducer;
import com.whoami.businessoperation.repository.BusinessApplicationRepository;
import com.whoami.businessoperation.repository.BusinessVerificationHistoryRepository;
import com.whoami.businessoperation.repository.BusinessVerificationRepository;
import com.whoami.businessoperation.service.BusinessApplicationService;
import com.whoami.businessoperation.service.BusinessAuditService;
import com.whoami.businessoperation.service.BusinessVerificationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BusinessVerificationServiceImpl
        implements BusinessVerificationService {

    private final BusinessVerificationRepository verificationRepository;
    private final BusinessVerificationHistoryRepository historyRepository;
    private final BusinessApplicationRepository businessApplicationRepository;
    private final BusinessAuditService businessAuditService;
    private final BusinessEventProducer businessEventProducer;
    private final BusinessApplicationService businessApplicationService;

    @Override
    public BusinessVerificationResponse createVerification(
            String businessId,
            String applicationId) {

        if (businessId == null || businessId.isBlank()) {
            throw new IllegalArgumentException("businessId is required");
        }

        if (applicationId == null || applicationId.isBlank()) {
            throw new IllegalArgumentException("applicationId is required");
        }

        if (verificationRepository.existsByBusinessId(businessId)) {
            log.info("Verification already exists for businessId={}, returning existing verification record", businessId);
            return getVerification(businessId);
        }

        LocalDateTime now = LocalDateTime.now();

        BusinessVerification verification = new BusinessVerification();
        verification.setBusinessId(businessId);
        verification.setApplicationId(applicationId);
        verification.setVerificationStatus(
                VerificationStatus.VERIFICATION_STARTED
        );
        verification.setStartedAt(now);

        BusinessVerification saved =
                verificationRepository.save(verification);

        saveHistory(
                saved,
                VerificationAction.STARTED,
                "SYSTEM",
                null,
                VerificationStatus.VERIFICATION_STARTED,
                "Verification process created",
                null
        );

        businessAuditService.log(
                saved.getBusinessId(),
                saved.getApplicationId(),
                AuditAction.VERIFICATION_STARTED,
                null,
                "SYSTEM",
                "Business verification process created",
                null,
                null
        );

        businessEventProducer.publishVerificationStartedEvent(
                saved,
                null,
                "SYSTEM",
                "Business verification process created"
        );

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BusinessVerificationResponse getVerification(String businessId) {

        return mapToResponse(getVerificationEntity(businessId));
    }

    @Override
    public BusinessVerificationResponse markPaymentCompleted(String businessId) {
        if (businessId == null || businessId.isBlank()) {
            throw new IllegalArgumentException("businessId is required");
        }

        BusinessVerification verification = getVerificationEntity(businessId);
        VerificationStatus previousStatus = verification.getVerificationStatus();

        verification.setVerificationStatus(VerificationStatus.PAYMENT_COMPLETED);
        BusinessVerification saved = verificationRepository.save(verification);

        saveHistory(
                saved,
                VerificationAction.PAYMENT_COMPLETED,
                "SYSTEM",
                previousStatus,
                VerificationStatus.PAYMENT_COMPLETED,
                "Verification fee payment completed",
                null
        );

        businessAuditService.log(
                saved.getBusinessId(),
                saved.getApplicationId(),
                AuditAction.VERIFICATION_STARTED,
                null,
                "SYSTEM",
                "Verification fee payment completed",
                null,
                null
        );

        return mapToResponse(saved);
    }

    @Override
    public BusinessVerificationResponse updateVerificationStatus(
            String businessId,
            VerificationStatus status) {

        if (businessId == null || businessId.isBlank()) {
            throw new IllegalArgumentException("businessId is required");
        }

        BusinessVerification verification = getVerificationEntity(businessId);
        VerificationStatus previousStatus = verification.getVerificationStatus();

        verification.setVerificationStatus(status);
        BusinessVerification saved = verificationRepository.save(verification);

        saveHistory(
                saved,
                VerificationAction.STARTED,
                "ADMIN",
                previousStatus,
                status,
                "Verification status updated to " + status,
                null
        );

        if (status == VerificationStatus.VERIFICATION_APPROVED) {
            saved.setApprovedAt(LocalDateTime.now());
            try {
                org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
                String shopUrl = "http://localhost:8084/internal/businesses/" + businessId + "/verification-status?verified=true";
                restTemplate.put(shopUrl, null);
                log.info("Notified shop-service REST API of verification approval for businessId={}", businessId);
            } catch (Exception e) {
                log.warn("Could not call shop-service REST API for verification approval: {}", e.getMessage());
            }
            businessEventProducer.publishVerificationApprovedEvent(
                    saved,
                    "ADMIN",
                    "ADMIN",
                    "Business verification approved"
            );
        } else if (status == VerificationStatus.REJECTED) {
            saved.setRejectedAt(LocalDateTime.now());
            businessEventProducer.publishVerificationRejectedEvent(
                    saved,
                    "ADMIN",
                    "ADMIN",
                    "Business verification rejected"
            );
        } else if (status == VerificationStatus.REUPLOAD_REQUIRED) {
            businessEventProducer.publishVerificationReuploadRequiredEvent(
                    saved,
                    "ADMIN",
                    "ADMIN",
                    "Business verification reupload required"
            );
        }

        return mapToResponse(saved);
    }

    @Override
    public BusinessVerificationResponse submitVerification(
            SubmitVerificationRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Submit verification request is required"
            );
        }

        if (request.getBusinessId() == null
                || request.getBusinessId().isBlank()) {
            throw new IllegalArgumentException("businessId is required");
        }

        if (request.getApplicationId() == null
                || request.getApplicationId().isBlank()) {
            throw new IllegalArgumentException("applicationId is required");
        }

        if (request.getVerificationVideoUrl() == null
                || request.getVerificationVideoUrl().isBlank()) {
            throw new IllegalArgumentException(
                    "verificationVideoUrl is required"
            );
        }

        if (request.getVerificationVideoPublicId() == null
                || request.getVerificationVideoPublicId().isBlank()) {
            throw new IllegalArgumentException(
                    "verificationVideoPublicId is required"
            );
        }

        BusinessVerification verification =
                getVerificationEntity(request.getBusinessId());

        if (!verification.getApplicationId()
                .equals(request.getApplicationId())) {
            throw new IllegalArgumentException(
                    "Application ID does not belong to this verification"
            );
        }

        VerificationStatus previousStatus =
                verification.getVerificationStatus();

        if (previousStatus != VerificationStatus.VERIFICATION_STARTED
                && previousStatus != VerificationStatus.PAYMENT_COMPLETED
                && previousStatus != VerificationStatus.DOCUMENT_UPLOADED
                && previousStatus != VerificationStatus.DOCUMENT_APPROVED
                && previousStatus != VerificationStatus.VIDEO_UPLOAD_REQUIRED
                && previousStatus != VerificationStatus.REUPLOAD_REQUIRED) {
            throw new IllegalStateException(
                    "Video cannot be submitted in current status: "
                            + previousStatus
            );
        }

        LocalDateTime now = LocalDateTime.now();

        verification.setVerificationVideoUrl(
                request.getVerificationVideoUrl()
        );
        verification.setVerificationVideoPublicId(
                request.getVerificationVideoPublicId()
        );

        if (request.getLatitude() != null && request.getLongitude() != null) {
            verification.setRecordingLatitude(request.getLatitude());
            verification.setRecordingLongitude(request.getLongitude());

            try {
                businessApplicationRepository.findByBusinessId(request.getBusinessId()).ifPresent(app -> {
                    if (app.getLatitude() != null && app.getLongitude() != null) {
                        double shopLat = app.getLatitude().doubleValue();
                        double shopLng = app.getLongitude().doubleValue();
                        double recLat = request.getLatitude();
                        double recLng = request.getLongitude();

                        double distMeters = calculateDistanceMeters(shopLat, shopLng, recLat, recLng);
                        verification.setDistanceMeters(distMeters);
                        verification.setLocationVerified(distMeters <= 1500.0); // Within 1.5 km

                        log.info("📍 [LOCATION CHECK] BusinessId={}: RecLoc=({}, {}), ShopLoc=({}, {}), Distance={}m, Verified={}",
                                request.getBusinessId(), recLat, recLng, shopLat, shopLng, distMeters, distMeters <= 1500.0);
                    }
                });
            } catch (Exception e) {
                log.warn("Could not verify recording location distance: {}", e.getMessage());
            }
        }

        verification.setSubmittedAt(now);
        verification.setVerificationStatus(
                VerificationStatus.VIDEO_UPLOADED
        );

        verification.setReviewerComment(null);
        verification.setReviewedBy(null);
        verification.setReviewedAt(null);
        verification.setApprovedAt(null);
        verification.setRejectedAt(null);

        BusinessVerification saved =
                verificationRepository.save(verification);

        saveHistory(
                saved,
                VerificationAction.VIDEO_SUBMITTED,
                "BUSINESS_OWNER",
                previousStatus,
                VerificationStatus.VIDEO_UPLOADED,
                "Verification video submitted",
                null
        );

        businessAuditService.log(
                saved.getBusinessId(),
                saved.getApplicationId(),
                AuditAction.VERIFICATION_SUBMITTED,
                null,
                "BUSINESS_OWNER",
                "Business verification video submitted",
                null,
                null
        );

        businessEventProducer.publishVerificationSubmittedEvent(
                saved,
                null,
                "BUSINESS_OWNER",
                "Business verification video submitted"
        );

        return mapToResponse(saved);
    }

    @Override
    public BusinessVerificationResponse reviewVerification(
            ReviewVerificationRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Review verification request is required"
            );
        }

        if (request.getBusinessId() == null
                || request.getBusinessId().isBlank()) {
            throw new IllegalArgumentException("businessId is required");
        }

        if (request.getVerificationStatus() == null) {
            throw new IllegalArgumentException(
                    "verificationStatus is required"
            );
        }

        if (request.getReviewedBy() == null
                || request.getReviewedBy().isBlank()) {
            throw new IllegalArgumentException("reviewedBy is required");
        }

        BusinessVerification verification =
                getVerificationEntity(request.getBusinessId());

        VerificationStatus previousStatus =
                verification.getVerificationStatus();

        if (previousStatus != VerificationStatus.VIDEO_UPLOADED
                && previousStatus != VerificationStatus.VIDEO_ADMIN_UNDER_REVIEW
                && previousStatus != VerificationStatus.DOCUMENT_APPROVED
                && previousStatus != VerificationStatus.DOCUMENT_UPLOADED
                && previousStatus != VerificationStatus.PAYMENT_COMPLETED
                && previousStatus != VerificationStatus.VERIFICATION_STARTED) {
            throw new IllegalStateException(
                    "Video verification cannot be reviewed in current status: "
                            + previousStatus
            );
        }

        VerificationStatus newStatus =
                request.getVerificationStatus();

        validateReviewStatus(newStatus);

        LocalDateTime now = LocalDateTime.now();

        verification.setVerificationStatus(newStatus);
        verification.setReviewerComment(request.getReviewerComment());
        verification.setReviewedBy(request.getReviewedBy());
        verification.setReviewedAt(now);

        switch (newStatus) {
            case VERIFICATION_APPROVED -> {
                verification.setApprovedAt(now);
                verification.setRejectedAt(null);

                try {
                    if (businessApplicationRepository.findByBusinessId(verification.getBusinessId()).isPresent()) {
                        businessApplicationService.approveApplication(
                                verification.getBusinessId(),
                                request.getReviewedBy()
                        );
                    } else {
                        log.info("No BusinessApplication record found for businessId={}, skipping application auto-approval.", verification.getBusinessId());
                    }
                } catch (Exception e) {
                    log.warn("Could not auto-approve application for businessId {}: {}", verification.getBusinessId(), e.getMessage());
                }
            }

            case REJECTED -> {
                verification.setRejectedAt(now);
                verification.setApprovedAt(null);
            }

            case REUPLOAD_REQUIRED -> {
                verification.setApprovedAt(null);
                verification.setRejectedAt(null);
            }

            case VIDEO_ADMIN_UNDER_REVIEW -> {
                verification.setApprovedAt(null);
                verification.setRejectedAt(null);
            }

            default -> throw new IllegalArgumentException(
                    "Unsupported review status: " + newStatus
            );
        }

        BusinessVerification saved =
                verificationRepository.save(verification);

        String comment = request.getReviewerComment() != null
                ? request.getReviewerComment()
                : "Verification status updated";

        saveHistory(
                saved,
                resolveVerificationAction(newStatus),
                request.getReviewedBy(),
                previousStatus,
                newStatus,
                comment,
                null
        );

        businessAuditService.log(
                saved.getBusinessId(),
                saved.getApplicationId(),
                resolveAuditAction(newStatus),
                request.getReviewedBy(),
                "ADMIN",
                comment,
                null,
                null
        );

        publishReviewEvent(
                saved,
                newStatus,
                request.getReviewedBy(),
                comment
        );

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusinessVerificationHistory> getVerificationHistory(
            String businessId) {

        if (businessId == null || businessId.isBlank()) {
            throw new IllegalArgumentException("businessId is required");
        }

        return historyRepository
                .findByBusinessIdOrderByCreatedAtDesc(businessId);
    }

    @Override
    public void deleteVerification(String verificationId) {

        if (verificationId == null || verificationId.isBlank()) {
            throw new IllegalArgumentException(
                    "verificationId is required"
            );
        }

        BusinessVerification verification =
                verificationRepository
                        .findByVerificationId(verificationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Business verification not found: "
                                                + verificationId
                                ));

        // Preserve history for auditing even if the verification is deleted.
        verificationRepository.delete(verification);
    }

    private BusinessVerification getVerificationEntity(String businessId) {

        if (businessId == null || businessId.isBlank()) {
            throw new IllegalArgumentException("businessId is required");
        }

        return verificationRepository.findByBusinessId(businessId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Business verification not found for businessId: "
                                        + businessId
                        ));
    }

    private void validateReviewStatus(VerificationStatus status) {

        if (status != VerificationStatus.VIDEO_ADMIN_UNDER_REVIEW
                && status != VerificationStatus.VERIFICATION_APPROVED
                && status != VerificationStatus.REJECTED
                && status != VerificationStatus.REUPLOAD_REQUIRED) {
            throw new IllegalArgumentException(
                    "Invalid video review status: " + status
            );
        }
    }

    private VerificationAction resolveVerificationAction(
            VerificationStatus status) {

        return switch (status) {
            case VIDEO_ADMIN_UNDER_REVIEW ->
                    VerificationAction.VIDEO_REVIEW_STARTED;
            case VERIFICATION_APPROVED ->
                    VerificationAction.VIDEO_APPROVED;
            case REJECTED ->
                    VerificationAction.VIDEO_REJECTED;
            case REUPLOAD_REQUIRED ->
                    VerificationAction.REUPLOAD_REQUESTED;
            case EXPIRED ->
                    VerificationAction.EXPIRED;
            default ->
                    VerificationAction.VERIFICATION_SUBMITTED;
        };
    }

    private AuditAction resolveAuditAction(VerificationStatus status) {

        return switch (status) {
            case VERIFICATION_APPROVED ->
                    AuditAction.VERIFICATION_APPROVED;
            case REJECTED ->
                    AuditAction.VERIFICATION_REJECTED;
            case REUPLOAD_REQUIRED ->
                    AuditAction.REUPLOAD_REQUESTED;
            default ->
                    AuditAction.VERIFICATION_SUBMITTED;
        };
    }

    private void publishReviewEvent(
            BusinessVerification verification,
            VerificationStatus status,
            String reviewedBy,
            String comment) {

        switch (status) {
            case VERIFICATION_APPROVED ->
                    businessEventProducer.publishVerificationApprovedEvent(
                            verification,
                            reviewedBy,
                            "ADMIN",
                            "Business verification approved"
                    );

            case REJECTED ->
                    businessEventProducer.publishVerificationRejectedEvent(
                            verification,
                            reviewedBy,
                            "ADMIN",
                            comment
                    );

            case REUPLOAD_REQUIRED ->
                    businessEventProducer
                            .publishVerificationReuploadRequiredEvent(
                                    verification,
                                    reviewedBy,
                                    "ADMIN",
                                    comment
                            );

            case VIDEO_ADMIN_UNDER_REVIEW -> {
                // Status and history are updated; no final-result event.
            }

            default -> throw new IllegalArgumentException(
                    "Unsupported review event status: " + status
            );
        }
    }

    private void saveHistory(
            BusinessVerification verification,
            VerificationAction action,
            String performedBy,
            VerificationStatus previousStatus,
            VerificationStatus newStatus,
            String comment,
            String description) {

        BusinessVerificationHistory history =
                new BusinessVerificationHistory();

        history.setBusinessId(verification.getBusinessId());
        history.setApplicationId(verification.getApplicationId());
        history.setVerificationId(verification.getVerificationId());
        history.setAction(action);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setPerformedBy(performedBy);
        history.setComment(
                comment != null ? comment : description
        );

        historyRepository.save(history);
    }

    private BusinessVerificationResponse mapToResponse(
            BusinessVerification verification) {

        BusinessVerificationResponse response =
                new BusinessVerificationResponse();

        response.setVerificationId(verification.getVerificationId());
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
        response.setRecordingLatitude(verification.getRecordingLatitude());
        response.setRecordingLongitude(verification.getRecordingLongitude());
        response.setLocationVerified(verification.getLocationVerified());
        response.setDistanceMeters(verification.getDistanceMeters());
        response.setReviewerComment(verification.getReviewerComment());
        response.setReviewedBy(verification.getReviewedBy());
        response.setStartedAt(verification.getStartedAt());
        response.setSubmittedAt(verification.getSubmittedAt());
        response.setReviewedAt(verification.getReviewedAt());
        response.setApprovedAt(verification.getApprovedAt());
        response.setRejectedAt(verification.getRejectedAt());
        response.setExpiresAt(verification.getExpiresAt());
        response.setCreatedAt(verification.getCreatedAt());
        response.setUpdatedAt(verification.getUpdatedAt());

        return response;
    }

    private double calculateDistanceMeters(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
