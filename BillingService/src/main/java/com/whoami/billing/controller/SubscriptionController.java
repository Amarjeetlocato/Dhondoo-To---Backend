package com.whoami.billing.controller;

import com.whoami.billing.dto.request.CreateSubscriptionRequest;
import com.whoami.billing.dto.response.SubscriptionResponse;
import com.whoami.billing.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/billing/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionResponse create(
            @Valid @RequestBody CreateSubscriptionRequest request) {

        return subscriptionService.create(request);
    }

    @GetMapping("/{id}")
    public SubscriptionResponse getById(
            @PathVariable UUID id) {

        return subscriptionService.getById(id);
    }

    @GetMapping("/gateway/{gatewaySubscriptionId}")
    public SubscriptionResponse getByGatewaySubscriptionId(
            @PathVariable String gatewaySubscriptionId) {

        return subscriptionService.getByGatewaySubscriptionId(
                gatewaySubscriptionId
        );
    }

    @PutMapping("/{subscriptionId}/activate")
    public SubscriptionResponse activate(
            @PathVariable UUID subscriptionId,
            @RequestParam(required = false)
            String gatewaySubscriptionId) {

        return subscriptionService.activate(
                subscriptionId,
                gatewaySubscriptionId
        );
    }

    @PutMapping("/{subscriptionId}/pause")
    public SubscriptionResponse pause(
            @PathVariable UUID subscriptionId) {

        return subscriptionService.pause(subscriptionId);
    }

    @PutMapping("/{subscriptionId}/cancel")
    public SubscriptionResponse cancel(
            @PathVariable UUID subscriptionId) {

        return subscriptionService.cancel(subscriptionId);
    }

    @PutMapping("/{subscriptionId}/expire")
    public SubscriptionResponse expire(
            @PathVariable UUID subscriptionId) {

        return subscriptionService.expire(subscriptionId);
    }
    
    @PutMapping("/{subscriptionId}/payment-failed")
    public SubscriptionResponse markPaymentFailed(
            @PathVariable UUID subscriptionId) {

        return subscriptionService.markPaymentFailed(subscriptionId);
    }
}