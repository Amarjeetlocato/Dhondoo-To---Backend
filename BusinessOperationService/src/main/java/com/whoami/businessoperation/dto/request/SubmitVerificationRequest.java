package com.whoami.businessoperation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitVerificationRequest {

    @NotNull(message = "Business ID is required")
    private UUID businessId;

    @NotNull(message = "Application ID is required")
    private UUID applicationId;

    @NotBlank(message = "Verification video URL is required")
    private String verificationVideoUrl;

    @NotBlank(message = "Verification video public ID is required")
    private String verificationVideoPublicId;
}