package com.locato.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessVerifiedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String eventId;

    private String businessId;

    private String shopId;

    private String userId;

    private String businessName;

    private String verificationStatus; // APPROVED, REJECTED, PENDING

    private String verifiedByAdminId;

    private String rejectionReason;

    private String notes;

    private LocalDateTime timestamp;
}
