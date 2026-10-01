package com.whoami.businessoperation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitBusinessApplicationRequest {

    @NotBlank(message = "Business ID is required")
    private String businessId;
}