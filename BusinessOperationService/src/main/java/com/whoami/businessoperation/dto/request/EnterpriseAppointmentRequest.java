package com.whoami.businessoperation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseAppointmentRequest {

    @NotBlank(message = "Business ID is required")
    private String businessId;

    private String businessName;

    @NotBlank(message = "Contact person name is required")
    private String contactPersonName;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String email;

    private String preferredDateTime;

    private String notes;
}
