package com.whoami.billing.service.impl;

import com.whoami.billing.domain.entity.BillingCycle;
import com.whoami.billing.domain.entity.BillingPlan;
import com.whoami.billing.dto.response.BillingPlanResponse;
import com.whoami.billing.repository.BillingPlanRepository;
import com.whoami.billing.service.BillingPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class BillingPlanServiceImpl implements BillingPlanService {

    private final BillingPlanRepository repository;

    @Override
    public BillingPlanResponse create(
            String name,
            String description,
            BigDecimal amount,
            String currency,
            String billingCycle,
            String planType) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Plan amount cannot be negative"
            );
        }

        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException(
                    "Currency is required"
            );
        }

        if (repository.findByName(name).isPresent()) {
            throw new IllegalStateException(
                    "Billing plan already exists: " + name
            );
        }

        BillingCycle cycle;

        try {
            cycle = BillingCycle.valueOf(
                    billingCycle.toUpperCase()
            );
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Invalid billing cycle: " + billingCycle
            );
        }

        BillingPlan plan = BillingPlan.builder()
                .name(name)
                .description(description)
                .amount(amount)
                .currency(currency.toUpperCase())
                .billingCycle(cycle)
                .planType(planType)
                .active(true)
                .build();

        return mapToResponse(repository.save(plan));
    }

    @Override
    @Transactional(readOnly = true)
    public BillingPlanResponse getById(UUID id) {

        return mapToResponse(
                repository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Billing plan not found: " + id
                                )
                        )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public BillingPlanResponse getByName(String name) {

        return mapToResponse(
                repository.findByName(name)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Billing plan not found: " + name
                                )
                        )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillingPlanResponse> getByPlanType(
            String planType) {

        return repository.findByPlanType(planType)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BillingPlanResponse> getActivePlans() {

        return repository.findByActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BillingPlanResponse activate(UUID id) {

        BillingPlan plan = findPlan(id);

        if (plan.isActive()) {
            return mapToResponse(plan);
        }

        plan.setActive(true);

        return mapToResponse(repository.save(plan));
    }

    @Override
    public BillingPlanResponse deactivate(UUID id) {

        BillingPlan plan = findPlan(id);

        if (!plan.isActive()) {
            return mapToResponse(plan);
        }

        plan.setActive(false);

        return mapToResponse(repository.save(plan));
    }

    private BillingPlan findPlan(UUID id) {

        return repository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Billing plan not found: " + id
                        )
                );
    }

    private BillingPlanResponse mapToResponse(
            BillingPlan entity) {

        return BillingPlanResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .amount(entity.getAmount())
                .currency(entity.getCurrency())
                .billingCycle(entity.getBillingCycle())
                .planType(entity.getPlanType())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
