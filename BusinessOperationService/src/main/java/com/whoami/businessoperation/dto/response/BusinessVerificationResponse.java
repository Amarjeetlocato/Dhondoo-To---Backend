package com.whoami.businessoperation.dto.response;

import com.whoami.businessoperation.domain.enums.VerificationStatus;
import lombok.*;

import java.time.LocalDateTime;

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