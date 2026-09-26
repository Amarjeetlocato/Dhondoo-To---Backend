package com.whoami.billing.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundRequest {

    @NotNull
    private UUID paymentId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @Size(max = 500)
    private String reason;
}