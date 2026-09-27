package com.whoami.businessoperation.service;

import com.whoami.businessoperation.domain.enums.CapabilityType;
import com.whoami.businessoperation.dto.request.UpdateBusinessCapabilityRequest;
import com.whoami.businessoperation.dto.response.BusinessCapabilityResponse;

import java.util.List;
import java.util.UUID;

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
            UUID businessId,
            CapabilityType capabilityType
    );

    List<BusinessCapabilityResponse> getBusinessCapabilities(
            UUID businessId
    );
}