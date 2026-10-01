package com.whoami.businessoperation.dto.response;

import com.whoami.businessoperation.domain.enums.BusinessApplicationStatus;
import com.whoami.businessoperation.domain.enums.BusinessOperationalStatus;
import com.whoami.businessoperation.domain.enums.BusinessType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessApplicationResponse {

    private String applicationId;

    private String businessId;

    private String ownerUserId;

    private BusinessType businessType;

    private String businessName;

    private String description;

    private String phone;

    private String email;

    private String address;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private BusinessApplicationStatus applicationStatus;

    private BusinessOperationalStatus operationalStatus;

    private LocalDateTime submittedAt;

    private LocalDateTime approvedAt;

    private LocalDateTime rejectedAt;

    private LocalDateTime suspendedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}