package com.whoami.businessoperation.service;

import com.whoami.businessoperation.dto.request.CreateBusinessApplicationRequest;
import com.whoami.businessoperation.dto.request.SubmitBusinessApplicationRequest;
import com.whoami.businessoperation.dto.request.UpdateBusinessApplicationRequest;
import com.whoami.businessoperation.dto.response.BusinessApplicationResponse;

import java.util.List;

public interface BusinessApplicationService {

    BusinessApplicationResponse createApplication(
            CreateBusinessApplicationRequest request
    );

    BusinessApplicationResponse getApplication(
            String businessId
    );

    List<BusinessApplicationResponse> getApplicationsByOwner(
            String ownerUserId
    );

    BusinessApplicationResponse updateApplication(
            String businessId,
            UpdateBusinessApplicationRequest request
    );

    BusinessApplicationResponse submitApplication(
            SubmitBusinessApplicationRequest request
    );

    BusinessApplicationResponse rejectApplication(
            String businessId,
            String reason,
            String performedBy
    );

    BusinessApplicationResponse approveApplication(
            String businessId,
            String performedBy
    );
}