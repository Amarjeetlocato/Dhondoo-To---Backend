package com.whoami.billing.controller;

import com.whoami.billing.dto.response.BillingPlanResponse;
import com.whoami.billing.service.BillingPlanService;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/billing/plans")
@RequiredArgsConstructor
public class BillingPlanController {

    private final BillingPlanService billingPlanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BillingPlanResponse create(
            @RequestParam @NotBlank @Size(max = 100)
            String name,

            @RequestParam(required = false)
            @Size(max = 500)
            String description,

            @RequestParam @NotNull
            @DecimalMin("0.00")
            BigDecimal amount,

            @RequestParam @NotBlank
            @Size(min = 3, max = 3)
            String currency,

            @RequestParam @NotBlank
            String billingCycle,

            @RequestParam @NotBlank
            @Size(max = 50)
            String planType
    ) {
        return billingPlanService.create(
                name,
                description,
                amount,
                currency,
                billingCycle,
                planType
        );
    }

    @GetMapping("/{id}")
    public BillingPlanResponse getById(
            @PathVariable UUID id) {

        return billingPlanService.getById(id);
    }

    @GetMapping("/name/{name}")
    public BillingPlanResponse getByName(
            @PathVariable String name) {

        return billingPlanService.getByName(name);
    }

    @GetMapping("/type/{planType}")
    public List<BillingPlanResponse> getByPlanType(
            @PathVariable String planType) {

        return billingPlanService.getByPlanType(planType);
    }

    @GetMapping("/active")
    public List<BillingPlanResponse> getActivePlans() {

        return billingPlanService.getActivePlans();
    }

    @PutMapping("/{id}/activate")
    public BillingPlanResponse activate(
            @PathVariable UUID id) {

        return billingPlanService.activate(id);
    }

    @PutMapping("/{id}/deactivate")
    public BillingPlanResponse deactivate(
            @PathVariable UUID id) {

        return billingPlanService.deactivate(id);
    }
}