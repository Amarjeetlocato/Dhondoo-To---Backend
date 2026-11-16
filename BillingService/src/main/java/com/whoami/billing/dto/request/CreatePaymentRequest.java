package com.whoami.billing.dto.request;

import com.whoami.billing.domain.entity.PaymentGateway;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePaymentRequest {

    @NotNull
    private UUID transactionId;

    @NotNull
    private PaymentGateway gateway;

    private BigDecimal amount;

    private String currency;

    @Size(max = 50)
    private String paymentMethod;
}