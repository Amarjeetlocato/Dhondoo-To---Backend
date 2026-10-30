package com.whoami.businessoperation.domain.enums;

public enum VerificationStatus {

    // Verification initialization
    VERIFICATION_STARTED,

    // Payment stage
    PAYMENT_INITIATED,
    PAYMENT_COMPLETED,

    // Document stage
    DOCUMENT_UPLOAD_REQUIRED,
    DOCUMENT_UPLOADED,
    ADMIN_UNDER_REVIEW,
    DOCUMENT_APPROVED,

    // Video verification stage
    VIDEO_UPLOAD_REQUIRED,
    VIDEO_UPLOADED,
    VIDEO_ADMIN_UNDER_REVIEW,

    // Final verification result
    VERIFICATION_APPROVED,
    REJECTED,
    REUPLOAD_REQUIRED,
    EXPIRED
}