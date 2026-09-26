package com.whoami.billing.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBillingRequest {

    @NotBlank
    private String customerId;

    @NotBlank
    @Size(max = 50)
    private String referenceType;

    @NotBlank
    @Size(max = 100)
    private String referenceId;

    @NotBlank
    @Size(max = 50)
    private String purpose;

    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal amount;

    @NotBlank
    @Size(min = 3, max = 3)
    private String currency;

    @NotBlank
    @Size(max = 150)
    private String idempotencyKey;
}