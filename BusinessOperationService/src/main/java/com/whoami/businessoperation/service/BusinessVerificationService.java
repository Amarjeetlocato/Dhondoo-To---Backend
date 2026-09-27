package com.whoami.businessoperation.service;

import com.whoami.businessoperation.dto.request.ReviewVerificationRequest;
import com.whoami.businessoperation.dto.request.SubmitVerificationRequest;
import com.whoami.businessoperation.dto.response.BusinessVerificationResponse;

import java.util.List;
import java.util.UUID;

public interface BusinessVerificationService {

    BusinessVerificationResponse createVerification(
            UUID businessId,
            UUID applicationId
    );

    BusinessVerificationResponse getVerification(
            UUID businessId
    );

    BusinessVerificationResponse submitVerification(
            SubmitVerificationRequest request
    );

    BusinessVerificationResponse reviewVerification(
            ReviewVerificationRequest request
    );

    List<?> getVerificationHistory(
            UUID businessId
    );
}