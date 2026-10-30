package com.whoami.launch.service;

import com.whoami.launch.enums.BusinessCapability;

public interface BusinessAccessService {

    void validateCreationAccess(
            String businessId,
            BusinessCapability capability,
            long currentCount
    );
}