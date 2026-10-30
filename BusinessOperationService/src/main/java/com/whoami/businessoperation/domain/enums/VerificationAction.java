package com.whoami.businessoperation.domain.enums;

public enum VerificationAction {

    // Verification lifecycle
    STARTED,
    SUBMITTED,
    VERIFICATION_SUBMITTED,
    REOPENED,

    // Payment events
    PAYMENT_INITIATED,
    PAYMENT_COMPLETED,
    PAYMENT_FAILED,

    // Document events
    DOCUMENT_SUBMITTED,
    DOCUMENT_APPROVED,
    DOCUMENT_REJECTED,

    // Video events
    VIDEO_REVIEW_STARTED,
    VIDEO_SUBMITTED,
    VIDEO_APPROVED,
    VIDEO_REJECTED,

    // Additional information and re-upload
    ADDITIONAL_INFORMATION_REQUESTED,
    REUPLOAD_REQUESTED,

    // Final outcomes
    APPROVED,
    REJECTED,
    EXPIRED
}
