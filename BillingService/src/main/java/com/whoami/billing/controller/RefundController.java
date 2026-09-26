package com.whoami.billing.controller;

import com.whoami.billing.dto.request.RefundRequest;
import com.whoami.billing.dto.response.RefundResponse;
import com.whoami.billing.service.RefundService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/billing/refunds")
@RequiredArgsConstructor
public class RefundController {

    private final RefundService refundService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RefundResponse create(
            @Valid @RequestBody RefundRequest request) {

        return refundService.create(request);
    }

    @GetMapping("/{id}")
    public RefundResponse getById(
            @PathVariable UUID id) {

        return refundService.getById(id);
    }

    @GetMapping("/gateway-refund/{gatewayRefundId}")
    public RefundResponse getByGatewayRefundId(
            @PathVariable String gatewayRefundId) {

        return refundService.getByGatewayRefundId(
                gatewayRefundId
        );
    }

    @PutMapping("/{refundId}/complete")
    public RefundResponse markRefundCompleted(
            @PathVariable UUID refundId,
            @RequestParam String gatewayRefundId) {

        return refundService.markRefundCompleted(
                refundId,
                gatewayRefundId
        );
    }
}