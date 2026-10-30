package com.whoami.launch.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.whoami.launch.entity.BusinessAccessState;
import com.whoami.launch.enums.BusinessCapability;
import com.whoami.launch.repository.BusinessAccessStateRepository;
import com.whoami.launch.service.BusinessAccessService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BusinessAccessServiceImpl
        implements BusinessAccessService {

    private static final long FREE_PRODUCT_LIMIT = 4;
    private static final long FREE_REEL_LIMIT = 3;
    private static final long FREE_SERVICE_LIMIT = 2;

    private final BusinessAccessStateRepository accessStateRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void validateCreationAccess(
            String businessId,
            BusinessCapability capability,
            long currentCount) {

        if (businessId == null || businessId.isBlank()) {
            throw new IllegalArgumentException(
                    "Business ID is required"
            );
        }

        if (capability == null) {
            throw new IllegalArgumentException(
                    "Business capability is required"
            );
        }

        if (currentCount < 0) {
            throw new IllegalArgumentException(
                    "Current count cannot be negative"
            );
        }

        BusinessAccessState accessState =
                accessStateRepository
                        .findByBusinessId(businessId)
                        .orElseGet(() -> {
                            BusinessAccessState state = new BusinessAccessState();
                            state.setBusinessId(businessId);
                            state.setVerified(false);
                            state.setSubscriptionActive(false);
                            return state;
                        });

        boolean stateChanged = false;

        // Auto-sync 1: Check verification from BusinessOperationService if not verified in local DB
        if (!accessState.isVerified()) {
            try {
                String verifUrl = "http://localhost:8090/api/business-operations/verifications/" + businessId;
                org.springframework.http.ResponseEntity<java.util.Map> verifRes = restTemplate.getForEntity(verifUrl, java.util.Map.class);
                if (verifRes.getStatusCode().is2xxSuccessful() && verifRes.getBody() != null) {
                    String status = (String) verifRes.getBody().get("verificationStatus");
                    if ("VERIFICATION_APPROVED".equalsIgnoreCase(status) || "APPROVED".equalsIgnoreCase(status)) {
                        accessState.setVerified(true);
                        stateChanged = true;
                        log.info("Auto-synced verification status to true in validateCreationAccess for businessId={}", businessId);
                    }
                }
            } catch (Exception e) {
                log.debug("Verification check skipped in validateCreationAccess for {}: {}", businessId, e.getMessage());
            }
        }

        // Auto-sync 2: Check subscription from BillingService if not active in local DB
        if (!accessState.isSubscriptionActive()) {
            try {
                String billingUrl = "http://localhost:8089/api/billing/subscriptions/customer/" + businessId;
                org.springframework.http.ResponseEntity<java.util.List> billingRes = restTemplate.getForEntity(billingUrl, java.util.List.class);
                if (billingRes.getStatusCode().is2xxSuccessful() && billingRes.getBody() != null) {
                    for (Object obj : billingRes.getBody()) {
                        if (obj instanceof java.util.Map map) {
                            String status = (String) map.get("status");
                            if ("ACTIVE".equalsIgnoreCase(status)) {
                                accessState.setSubscriptionActive(true);
                                stateChanged = true;
                                log.info("Auto-synced subscription status to active in validateCreationAccess for businessId={}", businessId);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.debug("Billing check skipped in validateCreationAccess for {}: {}", businessId, e.getMessage());
            }
        }

        if (stateChanged || accessState.getId() == null) {
            accessState = accessStateRepository.save(accessState);
        }

        /*
         * UNLIMITED CREATION: Verified + Active Subscription
         */
        if (accessState.isVerified() && accessState.isSubscriptionActive()) {
            return;
        }

        /*
         * FREE TIER CREATION: Allow creation if current count is within free limits
         */
        long freeLimit = getFreeLimit(capability);
        if (currentCount < freeLimit) {
            log.info("Allowed free tier creation for businessId={}, capability={}, currentCount={}, freeLimit={}",
                    businessId, capability, currentCount, freeLimit);
            return;
        }

        /*
         * Block creation if free limit exceeded and lacking verified/active subscription
         */
        String capabilityName = capability.name().toLowerCase();

        if (!accessState.isVerified() && !accessState.isSubscriptionActive()) {
            throw new IllegalStateException(
                    "You have reached the free limit (" + freeLimit + " " + capabilityName + "s). Please verify your business and upgrade your subscription plan to create more."
            );
        } else if (!accessState.isVerified()) {
            throw new IllegalStateException(
                    "You have reached the free limit (" + freeLimit + " " + capabilityName + "s). Please verify your business to create more."
            );
        } else {
            throw new IllegalStateException(
                    "You have reached the free limit (" + freeLimit + " " + capabilityName + "s). Please upgrade your subscription plan to create more."
            );
        }
    }

    private long getFreeLimit(
            BusinessCapability capability) {

        return switch (capability) {

            case PRODUCT ->
                    FREE_PRODUCT_LIMIT;

            case REEL ->
                    FREE_REEL_LIMIT;

            case SERVICE ->
                    FREE_SERVICE_LIMIT;
        };
    }
}
