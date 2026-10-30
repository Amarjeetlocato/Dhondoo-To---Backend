package com.whoami.businessoperation.service;

import com.whoami.businessoperation.domain.entity.BusinessUpdateRequest;
import com.whoami.businessoperation.dto.request.SubmitBusinessUpdateRequest;

import java.util.List;

public interface BusinessUpdateRequestService {

    BusinessUpdateRequest submitRequest(SubmitBusinessUpdateRequest request);

    List<BusinessUpdateRequest> getRequestsByBusiness(String businessId);

    BusinessUpdateRequest approveRequest(String requestId, String reviewedBy);

    BusinessUpdateRequest rejectRequest(String requestId, String reason, String reviewedBy);
}
