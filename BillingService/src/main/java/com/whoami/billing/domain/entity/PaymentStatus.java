package com.whoami.billing.domain.entity;

public enum PaymentStatus {

    CREATED,
    PENDING,
    SUCCESS,
    FAILED,
    CANCELLED,
    REFUNDED,
    PARTIALLY_REFUNDED
}