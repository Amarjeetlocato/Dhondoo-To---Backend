package com.whoami.businessoperation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitBusinessUpdateRequest {

    @NotBlank(message = "Business ID is required")
    private String businessId;

    private String requestedBusinessName;

    private String requestedPhone;

    private String requestedEmail;

    private String requestedAddress;

    private BigDecimal requestedLatitude;

    private BigDecimal requestedLongitude;

    private String reason;
}
