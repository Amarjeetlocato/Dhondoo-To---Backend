package com.whoami.businessoperation.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class SubmitVerificationRequest {

    @NotBlank(message = "Business ID is required")
    private String businessId;

    @NotBlank(message = "Application ID is required")
    private String applicationId;

    @NotBlank(message = "Verification video URL is required")
    private String verificationVideoUrl;

    @NotBlank(message = "Verification video public ID is required")
    private String verificationVideoPublicId;

    private Double latitude;

    private Double longitude;
}