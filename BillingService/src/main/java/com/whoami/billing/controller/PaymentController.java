package com.whoami.billing.controller;

import com.whoami.billing.dto.request.CreatePaymentRequest;
import com.whoami.billing.dto.response.PaymentResponse;
import com.whoami.billing.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/billing/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentResponse create(
            @Valid @RequestBody CreatePaymentRequest request) {

        return paymentService.create(request);
    }

    @GetMapping("/{id}")
    public PaymentResponse getById(
            @PathVariable UUID id) {

        return paymentService.getById(id);
    }

    @GetMapping("/gateway-order/{gatewayOrderId}")
    public PaymentResponse getByGatewayOrderId(
            @PathVariable String gatewayOrderId) {

        return paymentService.getByGatewayOrderId(
                gatewayOrderId
        );
    }

    @GetMapping("/gateway-payment/{gatewayPaymentId}")
    public PaymentResponse getByGatewayPaymentId(
            @PathVariable String gatewayPaymentId) {

        return paymentService.getByGatewayPaymentId(
                gatewayPaymentId
        );
    }

    @PutMapping("/{paymentId}/success")
    public PaymentResponse markPaymentSuccess(
            @PathVariable UUID paymentId,
            @RequestParam String gatewayPaymentId) {

        return paymentService.markPaymentSuccess(
                paymentId,
                gatewayPaymentId
        );
    }

    @PutMapping("/{paymentId}/failed")
    public PaymentResponse markPaymentFailed(
            @PathVariable UUID paymentId,
            @RequestParam(required = false) String reason) {

        return paymentService.markPaymentFailed(
                paymentId,
                reason
        );
    }

    @PutMapping("/{paymentId}/refunded")
    public PaymentResponse markAsRefunded(
            @PathVariable UUID paymentId) {

        return paymentService.markAsRefunded(paymentId);
    }

    @PutMapping("/{paymentId}/partially-refunded")
    public PaymentResponse markAsPartiallyRefunded(
            @PathVariable UUID paymentId) {

        return paymentService.markAsPartiallyRefunded(
                paymentId
        );
    }
    
    @PutMapping("/{paymentId}/retry")
    public PaymentResponse retryPayment(
            @PathVariable UUID paymentId) {

        return paymentService.retryPayment(paymentId);
    }
}
