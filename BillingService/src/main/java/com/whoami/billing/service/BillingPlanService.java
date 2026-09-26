package com.whoami.billing.service;

import java.util.List;
import java.util.UUID;

import com.whoami.billing.dto.response.BillingPlanResponse;

public interface BillingPlanService {

    BillingPlanResponse create(
            String name,
            String description,
            java.math.BigDecimal amount,
            String currency,
            String billingCycle,
            String planType
    );

    BillingPlanResponse getById(UUID id);

    BillingPlanResponse getByName(String name);

    List<BillingPlanResponse> getByPlanType(String planType);

    List<BillingPlanResponse> getActivePlans();

    BillingPlanResponse activate(UUID id);

    BillingPlanResponse deactivate(UUID id);
}