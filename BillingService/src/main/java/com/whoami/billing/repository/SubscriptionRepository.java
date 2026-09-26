package com.whoami.billing.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whoami.billing.domain.entity.Subscription;
import com.whoami.billing.domain.entity.SubscriptionStatus;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    List<Subscription> findByCustomerId(String customerId);

    List<Subscription> findByCustomerIdAndStatus(
            String customerId,
            SubscriptionStatus status
    );

    Optional<Subscription> findByGatewaySubscriptionId(
            String gatewaySubscriptionId
    );

    List<Subscription> findByPlanId(UUID planId);

    List<Subscription> findByStatusAndNextBillingDateLessThanEqual(
            SubscriptionStatus status,
            LocalDate date
    );
}