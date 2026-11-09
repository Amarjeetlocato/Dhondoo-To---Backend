package com.whoami.launch.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.whoami.launch.dto.BusinessResponseDTO;
import com.whoami.launch.dto.CustomerProfileResponseDTO;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.entity.Business;
import com.whoami.launch.entity.BusinessAccessState;
import com.whoami.launch.repository.BusinessAccessStateRepository;
import com.whoami.launch.service.BusinessService;
import com.whoami.launch.service.CustomerProfileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/businesses")
@Slf4j
public class BusinessControllerInternal {

    private final BusinessService businessService;
    private final CustomerProfileService customerProfileService;
    private final BusinessAccessStateRepository accessStateRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    public ResponseEntity<PageResponse<BusinessResponseDTO>> getAllBusinesses(
            Pageable pageable) {

        return ResponseEntity.ok(
                businessService.getAllBusinesses(pageable));
    }

    @GetMapping("/customers")
    public ResponseEntity<PageResponse<CustomerProfileResponseDTO>>
    getAllCustomers(Pageable pageable) {

        return ResponseEntity.ok(
                customerProfileService.getAllcustomers(pageable));
    }

    @GetMapping("/{businessId}")
    public ResponseEntity<Business> getBusiness(
            @PathVariable String businessId) {

        return businessService.getBusinessById(businessId)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @GetMapping("/{businessId}/access-state")
    public ResponseEntity<BusinessAccessState> getBusinessAccessState(
            @PathVariable String businessId) {

        BusinessAccessState state = accessStateRepository.findByBusinessId(businessId)
                .orElseGet(() -> {
                    BusinessAccessState s = new BusinessAccessState();
                    s.setBusinessId(businessId);
                    s.setVerified(false);
                    s.setSubscriptionActive(false);
                    return s;
                });

        boolean stateChanged = false;

        // Dynamic sync 1: Check verification status from BusinessOperationService if not yet verified
        if (!state.isVerified() || !state.isPaymentCompleted()) {
            try {
                String verifUrl = "http://localhost:8090/api/business-operations/verifications/" + businessId;
                ResponseEntity<java.util.Map> verifRes = restTemplate.getForEntity(verifUrl, java.util.Map.class);
                if (verifRes.getStatusCode().is2xxSuccessful() && verifRes.getBody() != null) {
                    String status = (String) verifRes.getBody().get("verificationStatus");
                    if ("VERIFICATION_APPROVED".equalsIgnoreCase(status) || "APPROVED".equalsIgnoreCase(status)) {
                        state.setVerified(true);
                        state.setPaymentCompleted(true);
                        stateChanged = true;
                        log.info("Auto-synced verification status to true for businessId={}", businessId);
                    } else if ("PAYMENT_COMPLETED".equalsIgnoreCase(status) || "VIDEO_SUBMITTED".equalsIgnoreCase(status) || "VIDEO_ADMIN_UNDER_REVIEW".equalsIgnoreCase(status) || "ADMIN_UNDER_REVIEW".equalsIgnoreCase(status)) {
                        state.setPaymentCompleted(true);
                        stateChanged = true;
                        log.info("Auto-synced paymentCompleted status to true for businessId={}", businessId);
                    }
                }
            } catch (Exception e) {
                log.debug("Verification service check skipped for {}: {}", businessId, e.getMessage());
            }
        }

        // Dynamic sync 2: Check subscription status from BillingService if not yet active
        if (!state.isSubscriptionActive()) {
            try {
                String billingUrl = "http://localhost:8089/api/billing/subscriptions/customer/" + businessId;
                ResponseEntity<java.util.List> billingRes = restTemplate.getForEntity(billingUrl, java.util.List.class);
                if (billingRes.getStatusCode().is2xxSuccessful() && billingRes.getBody() != null) {
                    for (Object obj : billingRes.getBody()) {
                        if (obj instanceof java.util.Map map) {
                            String status = (String) map.get("status");
                            if ("ACTIVE".equalsIgnoreCase(status)) {
                                state.setSubscriptionActive(true);
                                stateChanged = true;
                                log.info("Auto-synced subscription status to active for businessId={}", businessId);
                                break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.debug("Billing service check skipped for {}: {}", businessId, e.getMessage());
            }
        }

        if (stateChanged || state.getId() == null) {
            state = accessStateRepository.save(state);
        }

        return ResponseEntity.ok(state);
    }

    @PutMapping("/{businessId}/payment-status")
    public ResponseEntity<BusinessAccessState> updatePaymentStatus(
            @PathVariable String businessId,
            @RequestParam boolean paymentCompleted) {

        BusinessAccessState state = accessStateRepository.findByBusinessId(businessId)
                .orElseGet(() -> {
                    BusinessAccessState s = new BusinessAccessState();
                    s.setBusinessId(businessId);
                    return s;
                });

        state.setPaymentCompleted(paymentCompleted);
        BusinessAccessState saved = accessStateRepository.save(state);
        log.info("Updated payment status via REST for businessId={}, paymentCompleted={}", businessId, paymentCompleted);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{businessId}/verification-status")
    public ResponseEntity<BusinessAccessState> updateVerificationStatus(
            @PathVariable String businessId,
            @RequestParam boolean verified) {

        BusinessAccessState state = accessStateRepository.findByBusinessId(businessId)
                .orElseGet(() -> {
                    BusinessAccessState s = new BusinessAccessState();
                    s.setBusinessId(businessId);
                    return s;
                });

        state.setVerified(verified);
        BusinessAccessState saved = accessStateRepository.save(state);
        log.info("Updated verification status via REST for businessId={}, verified={}", businessId, verified);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{businessId}/subscription-status")
    public ResponseEntity<BusinessAccessState> updateSubscriptionStatus(
            @PathVariable String businessId,
            @RequestParam boolean active) {

        BusinessAccessState state = accessStateRepository.findByBusinessId(businessId)
                .orElseGet(() -> {
                    BusinessAccessState s = new BusinessAccessState();
                    s.setBusinessId(businessId);
                    return s;
                });

        state.setSubscriptionActive(active);
        BusinessAccessState saved = accessStateRepository.save(state);
        log.info("Updated subscription status via REST for businessId={}, active={}", businessId, active);
        return ResponseEntity.ok(saved);
    }
}
