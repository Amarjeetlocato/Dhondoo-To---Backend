package com.whoami.businessoperation.dto.response;

import com.whoami.businessoperation.domain.enums.VerificationStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessVerificationResponse {

    private UUID id;

    private UUID businessId;

    private UUID applicationId;

    private VerificationStatus verificationStatus;

    private String verificationVideoUrl;

    private String verificationVideoPublicId;

    private String reviewerComment;

    private UUID reviewedBy;

    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

    private LocalDateTime reviewedAt;

    private LocalDateTime approvedAt;

    private LocalDateTime rejectedAt;

    private LocalDateTime expiresAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}