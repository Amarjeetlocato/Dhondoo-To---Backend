package com.whoami.businessoperation.dto.request;

import com.whoami.businessoperation.domain.enums.VerificationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewVerificationRequest {

    @NotNull(message = "Business ID is required")
    private UUID businessId;

    @NotNull(message = "Verification status is required")
    private VerificationStatus verificationStatus;

    @Size(
            max = 1000,
            message = "Reviewer comment must not exceed 1000 characters"
    )
    private String reviewerComment;

    @NotNull(message = "Reviewer ID is required")
    private UUID reviewedBy;
}