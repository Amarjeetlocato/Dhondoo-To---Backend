package com.whoami.launch.order.orders.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whoami.launch.order.orders.entity.AnalyticsTransaction;

public interface AnalyticsTransactionRepository
        extends JpaRepository<AnalyticsTransaction, Long> {

    List<AnalyticsTransaction> findByShopIdAndSoldAtBetween(
            String shopId,
            LocalDateTime start,
            LocalDateTime end
    );
}