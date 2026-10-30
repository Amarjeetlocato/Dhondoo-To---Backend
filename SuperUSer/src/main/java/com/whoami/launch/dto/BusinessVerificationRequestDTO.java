package com.whoami.launch.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessVerificationRequestDTO {

    @NotBlank(message = "Business ID / Shop ID is required")
    private String businessId;

    private String shopId;

    @NotBlank(message = "Verification status is required")
    private String verificationStatus; // APPROVED, REJECTED, PENDING

    private String rejectionReason;

    private String notes;

    private String verifiedByAdminId;

    public String getEffectiveBusinessId() {
        return businessId != null ? businessId : shopId;
    }
}
