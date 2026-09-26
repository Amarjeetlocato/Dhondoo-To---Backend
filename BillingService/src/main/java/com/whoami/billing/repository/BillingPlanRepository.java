package com.whoami.billing.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whoami.billing.domain.entity.BillingPlan;

public interface BillingPlanRepository extends JpaRepository<BillingPlan, UUID> {

    Optional<BillingPlan> findByName(String name);

    List<BillingPlan> findByPlanType(String planType);

    List<BillingPlan> findByActiveTrue();
}