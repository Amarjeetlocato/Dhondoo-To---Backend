package com.whoami.billing.controller;

import com.whoami.billing.dto.request.CreateBillingRequest;
import com.whoami.billing.dto.response.BillingTransactionResponse;
import com.whoami.billing.service.BillingTransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/billing/transactions")
@RequiredArgsConstructor
public class BillingTransactionController {

    private final BillingTransactionService billingTransactionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BillingTransactionResponse create(
            @Valid @RequestBody CreateBillingRequest request) {

        return billingTransactionService.create(request);
    }

    @GetMapping("/{id}")
    public BillingTransactionResponse getById(
            @PathVariable UUID id) {

        return billingTransactionService.getById(id);
    }

    @GetMapping("/transaction/{transactionId}")
    public BillingTransactionResponse getByTransactionId(
            @PathVariable String transactionId) {

        return billingTransactionService.getByTransactionId(
                transactionId
        );
    }

    @GetMapping("/idempotency/{idempotencyKey}")
    public BillingTransactionResponse getByIdempotencyKey(
            @PathVariable String idempotencyKey) {

        return billingTransactionService.getByIdempotencyKey(
                idempotencyKey
        );
    }
    
    @PutMapping("/{id}/paid")
    public void markAsPaid(
            @PathVariable UUID id) {

        billingTransactionService.markAsPaid(id);
    }
}