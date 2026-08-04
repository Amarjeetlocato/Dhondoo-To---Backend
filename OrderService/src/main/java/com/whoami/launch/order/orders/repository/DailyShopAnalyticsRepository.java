package com.whoami.launch.order.orders.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whoami.launch.order.orders.entity.DailyShopAnalytics;

public interface DailyShopAnalyticsRepository
        extends JpaRepository<DailyShopAnalytics, Long> {

    Optional<DailyShopAnalytics> findByShopIdAndAnalyticsDate(
            String shopId,
            LocalDate date
    );
}