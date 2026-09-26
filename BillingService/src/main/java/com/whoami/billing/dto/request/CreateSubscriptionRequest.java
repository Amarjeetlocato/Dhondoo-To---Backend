package com.whoami.billing.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSubscriptionRequest {

    @NotBlank
    private String customerId;

    @NotNull
    private UUID planId;

    @NotBlank
    @Size(max = 30)
    private String gateway;

    @Size(max = 150)
    private String gatewaySubscriptionId;
}