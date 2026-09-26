package com.whoami.billing.domain.entity;

public enum SubscriptionStatus {

    PENDING,
    ACTIVE,
    PAUSED,
    PAYMENT_FAILED,
    CANCELLED,
    EXPIRED
}