package com.whoami.launch.order.orders.repository;

import com.whoami.launch.order.orders.entity.AnalyticsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AnalyticsTransactionRepository
        extends JpaRepository<AnalyticsTransaction, Long> {

    List<AnalyticsTransaction> findByBusinessIdAndSoldAtBetween(
            String businessId,
            LocalDateTime start,
            LocalDateTime end);
}