package com.whoami.businessoperation.service;

import com.whoami.businessoperation.dto.request.CreateBusinessApplicationRequest;
import com.whoami.businessoperation.dto.request.SubmitBusinessApplicationRequest;
import com.whoami.businessoperation.dto.request.UpdateBusinessApplicationRequest;
import com.whoami.businessoperation.dto.response.BusinessApplicationResponse;

import java.util.List;
import java.util.UUID;

public interface BusinessApplicationService {

    BusinessApplicationResponse createApplication(
            CreateBusinessApplicationRequest request
    );

    BusinessApplicationResponse getApplication(
            UUID businessId
    );

    List<BusinessApplicationResponse> getApplicationsByOwner(
            UUID ownerUserId
    );

    BusinessApplicationResponse updateApplication(
            UUID businessId,
            UpdateBusinessApplicationRequest request
    );

    BusinessApplicationResponse submitApplication(
            SubmitBusinessApplicationRequest request
    );

    BusinessApplicationResponse rejectApplication(
            UUID businessId,
            String reason,
            UUID performedBy
    );

    BusinessApplicationResponse approveApplication(
            UUID businessId,
            UUID performedBy
    );
}