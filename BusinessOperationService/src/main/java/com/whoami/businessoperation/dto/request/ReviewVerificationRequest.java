package com.whoami.businessoperation.dto.request;

import com.whoami.businessoperation.domain.enums.VerificationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewVerificationRequest {

    @NotBlank(message = "Business ID is required")
    private String businessId;

    @NotNull(message = "Verification status is required")
    private VerificationStatus verificationStatus;

    @Size(
            max = 1000,
            message = "Reviewer comment must not exceed 1000 characters"
    )
    private String reviewerComment;

    @NotBlank(message = "Reviewer ID is required")
    private String reviewedBy;
}