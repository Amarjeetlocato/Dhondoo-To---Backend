package com.whoami.businessoperation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitBusinessApplicationRequest {

    @NotNull(message = "Business ID is required")
    private UUID businessId;
}