package com.whoami.billing.domain.entity;


public enum BillingTransactionStatus {

    CREATED,
    PENDING,
    PAYMENT_REQUIRED,
    PAID,
    FAILED,
    CANCELLED,
    EXPIRED,
    REFUNDED,
    PARTIALLY_REFUNDED
}