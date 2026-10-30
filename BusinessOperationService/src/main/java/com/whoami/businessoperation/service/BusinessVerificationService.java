
package com.whoami.businessoperation.service;

import java.util.List;

import com.whoami.businessoperation.domain.entity.BusinessVerificationHistory;
import com.whoami.businessoperation.dto.request.ReviewVerificationRequest;
import com.whoami.businessoperation.dto.request.SubmitVerificationRequest;
import com.whoami.businessoperation.dto.response.BusinessVerificationResponse;

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

    BusinessVerificationResponse markPaymentCompleted(
            String businessId
    );

    BusinessVerificationResponse updateVerificationStatus(
            String businessId,
            com.whoami.businessoperation.domain.enums.VerificationStatus status
    );

    void deleteVerification(String verificationId);
}