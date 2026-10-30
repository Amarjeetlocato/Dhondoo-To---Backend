package com.locato.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String eventId;

    private String paymentId;

    private String orderId;

    private String userId;

    private String businessId;

    private String shopId;

    private BigDecimal amount;

    private String currency;

    private String status; // SUCCESS, FAILED, PENDING, REFUNDED

    private String paymentMethod; // RAZORPAY, CARD, UPI, NETBANKING, COD

    private String paymentType; // VERIFICATION, ORDER, SUBSCRIPTION

    private String transactionRef;

    private String failureReason;

    private LocalDateTime timestamp;
}
