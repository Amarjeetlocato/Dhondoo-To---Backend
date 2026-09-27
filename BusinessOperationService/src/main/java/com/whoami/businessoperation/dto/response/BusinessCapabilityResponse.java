package com.whoami.businessoperation.dto.response;

import com.whoami.businessoperation.domain.enums.CapabilityStatus;
import com.whoami.businessoperation.domain.enums.CapabilityType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessCapabilityResponse {

    private UUID id;

    private UUID businessId;

    private CapabilityType capabilityType;

    private CapabilityStatus capabilityStatus;

    private UUID enabledBy;

    private UUID disabledBy;

    private String reason;

    private LocalDateTime enabledAt;

    private LocalDateTime disabledAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}