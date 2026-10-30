package com.whoami.billing.service.impl;

import com.whoami.billing.domain.entity.BillingPlan;
import com.whoami.billing.domain.entity.PaymentGateway;
import com.whoami.billing.domain.entity.Subscription;
import com.whoami.billing.domain.entity.SubscriptionStatus;
import com.whoami.billing.dto.request.CreateSubscriptionRequest;
import com.whoami.billing.dto.response.SubscriptionResponse;
import com.whoami.billing.repository.BillingPlanRepository;
import com.whoami.billing.repository.SubscriptionRepository;
import com.whoami.billing.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository repository;

    private final BillingPlanRepository billingPlanRepository;

    @Override
    public SubscriptionResponse create(
            CreateSubscriptionRequest request) {

        validateBusinessVerification(request.getCustomerId());

        BillingPlan plan = billingPlanRepository.findById(
                request.getPlanId()
        ).orElseThrow(() ->
                new IllegalArgumentException(
                        "Billing plan not found: " + request.getPlanId()
                )
        );

        if (!plan.isActive()) {
            throw new IllegalStateException(
                    "Billing plan is not active: " + plan.getName()
            );
        }

        PaymentGateway gateway;

        try {
            gateway = PaymentGateway.valueOf(
                    request.getGateway().toUpperCase()
            );
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Unsupported payment gateway: "
                            + request.getGateway()
            );
        }

        repository.findByCustomerIdAndStatus(
                        request.getCustomerId(),
                        SubscriptionStatus.ACTIVE
                ).stream()
                .filter(subscription ->
                        subscription.getPlanId().equals(request.getPlanId())
                )
                .findFirst()
                .ifPresent(existing -> {
                    throw new IllegalStateException(
                            "Customer already has an active subscription "
                                    + "for this plan"
                    );
                });

        LocalDate startDate = LocalDate.now();

        LocalDate nextBillingDate = switch (plan.getBillingCycle()) {
            case ONE_TIME -> null;
            case MONTHLY -> startDate.plusMonths(1);
            case YEARLY -> startDate.plusYears(1);
        };

        Subscription subscription = Subscription.builder()
                .customerId(request.getCustomerId())
                .planId(plan.getId())
                .gateway(gateway)
                .gatewaySubscriptionId(
                        request.getGatewaySubscriptionId()
                )
                .status(SubscriptionStatus.PENDING)
                .startDate(startDate)
                .nextBillingDate(nextBillingDate)
                .build();

        return mapToResponse(
                repository.save(subscription)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponse getById(UUID id) {

        return mapToResponse(
                repository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Subscription not found: " + id
                                )
                        )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubscriptionResponse> getByCustomerId(String customerId) {
        return repository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SubscriptionResponse getByGatewaySubscriptionId(
            String gatewaySubscriptionId) {

        return mapToResponse(
                repository.findByGatewaySubscriptionId(
                                gatewaySubscriptionId
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Subscription not found: "
                                                + gatewaySubscriptionId
                                )
                        )
        );
    }

    @Override
    public SubscriptionResponse activate(
            UUID subscriptionId,
            String gatewaySubscriptionId) {

        Subscription subscription = findSubscription(subscriptionId);

        validateBusinessVerification(subscription.getCustomerId());

        if (subscription.getStatus() == SubscriptionStatus.CANCELLED
                || subscription.getStatus() == SubscriptionStatus.EXPIRED) {

            throw new IllegalStateException(
                    "Cannot activate subscription with status: "
                            + subscription.getStatus()
            );
        }

        LocalDate activationDate = LocalDate.now();

        subscription.setStatus(SubscriptionStatus.ACTIVE);

        if (gatewaySubscriptionId != null
                && !gatewaySubscriptionId.isBlank()) {

            subscription.setGatewaySubscriptionId(
                    gatewaySubscriptionId
            );
        }

        subscription.setStartDate(activationDate);

        BillingPlan plan = billingPlanRepository.findById(
                subscription.getPlanId()
        ).orElseThrow(() ->
                new IllegalArgumentException(
                        "Billing plan not found: "
                                + subscription.getPlanId()
                )
        );

        LocalDate nextBillingDate = switch (plan.getBillingCycle()) {
            case ONE_TIME -> null;
            case MONTHLY -> activationDate.plusMonths(1);
            case YEARLY -> activationDate.plusYears(1);
        };

        subscription.setNextBillingDate(nextBillingDate);

        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            String shopUrl = "http://localhost:8084/internal/businesses/" + subscription.getCustomerId() + "/subscription-status?active=true";
            restTemplate.put(shopUrl, null);
        } catch (Exception ignored) {
        }

        return mapToResponse(
                repository.save(subscription)
        );
    }

    @Override
    public SubscriptionResponse pause(UUID subscriptionId) {

        Subscription subscription = findSubscription(
                subscriptionId
        );

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {

            throw new IllegalStateException(
                    "Only active subscription can be paused"
            );
        }

        subscription.setStatus(SubscriptionStatus.PAUSED);

        return mapToResponse(
                repository.save(subscription)
        );
    }

    @Override
    public SubscriptionResponse cancel(UUID subscriptionId) {

        Subscription subscription = findSubscription(
                subscriptionId
        );

        if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
            return mapToResponse(subscription);
        }

        subscription.setStatus(SubscriptionStatus.CANCELLED);
        subscription.setEndDate(LocalDate.now());

        return mapToResponse(
                repository.save(subscription)
        );
    }

    @Override
    public SubscriptionResponse expire(UUID subscriptionId) {

        Subscription subscription = findSubscription(
                subscriptionId
        );

        subscription.setStatus(SubscriptionStatus.EXPIRED);
        subscription.setEndDate(LocalDate.now());

        return mapToResponse(
                repository.save(subscription)
        );
    }

    @Override
    public SubscriptionResponse markPaymentFailed(UUID subscriptionId) {

        Subscription subscription = findSubscription(
                subscriptionId
        );

        if (subscription.getStatus() == SubscriptionStatus.CANCELLED
                || subscription.getStatus() == SubscriptionStatus.EXPIRED) {

            throw new IllegalStateException(
                    "Cannot mark payment failed for subscription with status: "
                            + subscription.getStatus()
            );
        }

        subscription.setStatus(SubscriptionStatus.PAYMENT_FAILED);

        return mapToResponse(
                repository.save(subscription)
        );
    }

    @Override
    public SubscriptionResponse renew(UUID subscriptionId) {

        Subscription subscription = findSubscription(
                subscriptionId
        );

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE
                && subscription.getStatus() != SubscriptionStatus.PAYMENT_FAILED) {

            throw new IllegalStateException(
                    "Only active or payment-failed subscription can be renewed"
            );
        }

        BillingPlan plan = billingPlanRepository.findById(
                subscription.getPlanId()
        ).orElseThrow(() ->
                new IllegalArgumentException(
                        "Billing plan not found: "
                                + subscription.getPlanId()
                )
        );

        if (!plan.isActive()) {
            throw new IllegalStateException(
                    "Billing plan is not active: " + plan.getName()
            );
        }

        if (plan.getBillingCycle().name().equals("ONE_TIME")) {
            throw new IllegalStateException(
                    "One-time subscription cannot be renewed"
            );
        }

        LocalDate currentBillingDate =
                subscription.getNextBillingDate();

        if (currentBillingDate == null) {
            throw new IllegalStateException(
                    "Next billing date is not set for subscription: "
                            + subscriptionId
            );
        }

        LocalDate nextBillingDate = switch (plan.getBillingCycle()) {
            case MONTHLY -> currentBillingDate.plusMonths(1);
            case YEARLY -> currentBillingDate.plusYears(1);
            case ONE_TIME -> throw new IllegalStateException(
                    "One-time subscription cannot be renewed"
            );
        };

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setNextBillingDate(nextBillingDate);

        return mapToResponse(
                repository.save(subscription)
        );
    }

    private Subscription findSubscription(UUID subscriptionId) {

        return repository.findById(subscriptionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Subscription not found: "
                                        + subscriptionId
                        )
                );
    }

    private void validateBusinessVerification(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return;
        }

        try {
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            String url = "http://localhost:8084/internal/businesses/" + customerId + "/access-state";
            org.springframework.http.ResponseEntity<java.util.Map> response = restTemplate.getForEntity(url, java.util.Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Boolean verified = (Boolean) response.getBody().get("verified");
                if (Boolean.FALSE.equals(verified)) {
                    throw new IllegalStateException("Business owner must be verified before purchasing or activating a subscription.");
                }
            }
        } catch (IllegalStateException e) {
            throw e;
        } catch (Exception ignored) {
        }
    }

    private SubscriptionResponse mapToResponse(
            Subscription entity) {

        return SubscriptionResponse.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .planId(entity.getPlanId())
                .gateway(entity.getGateway())
                .gatewaySubscriptionId(
                        entity.getGatewaySubscriptionId()
                )
                .status(entity.getStatus())
                .startDate(entity.getStartDate())
                .nextBillingDate(entity.getNextBillingDate())
                .endDate(entity.getEndDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}