package com.whoami.businessoperation.dto.response;

import java.time.LocalDateTime;

import com.whoami.businessoperation.domain.enums.VerificationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessVerificationResponse {

    private String verificationId;

    private String businessId;

    private String applicationId;

    private VerificationStatus verificationStatus;

    private String verificationVideoUrl;

    private String verificationVideoPublicId;

    private Double recordingLatitude;

    private Double recordingLongitude;

    private Boolean locationVerified;

    private Double distanceMeters;

    private String reviewerComment;

    private String reviewedBy;

    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

    private LocalDateTime reviewedAt;

    private LocalDateTime approvedAt;

    private LocalDateTime rejectedAt;

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}