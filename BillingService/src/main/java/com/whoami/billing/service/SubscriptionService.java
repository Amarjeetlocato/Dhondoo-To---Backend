package com.whoami.billing.service;

import java.util.UUID;

import com.whoami.billing.dto.request.CreateSubscriptionRequest;
import com.whoami.billing.dto.response.SubscriptionResponse;

public interface SubscriptionService {

    SubscriptionResponse create(CreateSubscriptionRequest request);

    SubscriptionResponse getById(UUID id);

    SubscriptionResponse getByGatewaySubscriptionId(
            String gatewaySubscriptionId
    );

    SubscriptionResponse activate(
            UUID subscriptionId,
            String gatewaySubscriptionId
    );

    SubscriptionResponse pause(UUID subscriptionId);

    SubscriptionResponse cancel(UUID subscriptionId);

    SubscriptionResponse expire(UUID subscriptionId);

    SubscriptionResponse markPaymentFailed(UUID subscriptionId);

    SubscriptionResponse renew(UUID subscriptionId);
}