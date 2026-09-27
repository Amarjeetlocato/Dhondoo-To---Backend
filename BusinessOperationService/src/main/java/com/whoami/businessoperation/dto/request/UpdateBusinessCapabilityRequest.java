package com.whoami.businessoperation.dto.request;

import com.whoami.businessoperation.domain.enums.CapabilityType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBusinessCapabilityRequest {

    @NotNull(message = "Business ID is required")
    private UUID businessId;

    @NotNull(message = "Capability type is required")
    private CapabilityType capabilityType;

    @NotNull(message = "Performed by is required")
    private UUID performedBy;

    @Size(
            max = 1000,
            message = "Reason must not exceed 1000 characters"
    )
    private String reason;
}