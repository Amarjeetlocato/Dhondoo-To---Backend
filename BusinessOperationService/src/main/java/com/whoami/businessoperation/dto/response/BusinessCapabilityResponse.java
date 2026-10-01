package com.whoami.businessoperation.dto.response;

import com.whoami.businessoperation.domain.enums.CapabilityStatus;
import com.whoami.businessoperation.domain.enums.CapabilityType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessCapabilityResponse {

    private String capabilityId;

    private String businessId;

    private CapabilityType capabilityType;

    private CapabilityStatus capabilityStatus;

    private String enabledBy;

    private String disabledBy;

    private String reason;

    private LocalDateTime enabledAt;

    private LocalDateTime disabledAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}