package com.whoami.launch.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessDocumentDTO {

    private String documentId;
    private String businessId;
    private String shopId;
    private String userId;

    private String documentType; // GST, PAN, BUSINESS_LICENSE, ID_PROOF
    private String documentNumber;
    private String documentUrl;
    private String verificationStatus; // PENDING, APPROVED, REJECTED
    private String rejectionReason;

    private LocalDateTime uploadedAt;
    private LocalDateTime verifiedAt;
}
