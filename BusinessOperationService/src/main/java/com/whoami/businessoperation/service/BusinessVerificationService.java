package com.whoami.businessoperation.service;

import com.whoami.businessoperation.domain.entity.BusinessVerificationHistory;
import com.whoami.businessoperation.dto.request.ReviewVerificationRequest;
import com.whoami.businessoperation.dto.request.SubmitVerificationRequest;
import com.whoami.businessoperation.dto.response.BusinessVerificationResponse;

import java.util.List;

public interface BusinessVerificationService {

    BusinessVerificationResponse createVerification(
            String businessId,
            String applicationId
    );

    BusinessVerificationResponse getVerification(
            String businessId
    );

    BusinessVerificationResponse submitVerification(
            SubmitVerificationRequest request
    );

    BusinessVerificationResponse reviewVerification(
            ReviewVerificationRequest request
    );

    List<BusinessVerificationHistory> getVerificationHistory(
            String businessId
    );
}