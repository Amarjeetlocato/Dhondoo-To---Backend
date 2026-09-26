package com.whoami.billing.service;

import com.whoami.billing.dto.request.RefundRequest;
import com.whoami.billing.dto.response.RefundResponse;

import java.util.UUID;

public interface RefundService {

    RefundResponse create(RefundRequest request);

    RefundResponse getById(UUID id);

    RefundResponse getByGatewayRefundId(String gatewayRefundId);

    RefundResponse markRefundCompleted(
            UUID refundId,
            String gatewayRefundId
    );
}