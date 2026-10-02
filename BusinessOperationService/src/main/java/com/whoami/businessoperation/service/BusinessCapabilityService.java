package com.whoami.businessoperation.service;

import java.util.List;

import com.whoami.businessoperation.domain.enums.CapabilityType;
import com.whoami.businessoperation.dto.request.UpdateBusinessCapabilityRequest;
import com.whoami.businessoperation.dto.response.BusinessCapabilityResponse;

public interface BusinessCapabilityService {

    BusinessCapabilityResponse enableCapability(
            UpdateBusinessCapabilityRequest request
    );

    BusinessCapabilityResponse disableCapability(
            UpdateBusinessCapabilityRequest request
    );

    BusinessCapabilityResponse suspendCapability(
            UpdateBusinessCapabilityRequest request
    );

    BusinessCapabilityResponse getCapability(
            String businessId,
            CapabilityType capabilityType
    );

    List<BusinessCapabilityResponse> getBusinessCapabilities(
            String businessId
    );
}