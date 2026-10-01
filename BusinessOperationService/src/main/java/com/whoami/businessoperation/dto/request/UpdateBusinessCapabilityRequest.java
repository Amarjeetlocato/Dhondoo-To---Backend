package com.whoami.businessoperation.dto.request;

import com.whoami.businessoperation.domain.enums.CapabilityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBusinessCapabilityRequest {

    @NotBlank(message = "Business ID is required")
    private String businessId;

    @NotNull(message = "Capability type is required")
    private CapabilityType capabilityType;

    @NotBlank(message = "Performed by is required")
    private String performedBy;

    @Size(
            max = 1000,
            message = "Reason must not exceed 1000 characters"
    )
    private String reason;
}