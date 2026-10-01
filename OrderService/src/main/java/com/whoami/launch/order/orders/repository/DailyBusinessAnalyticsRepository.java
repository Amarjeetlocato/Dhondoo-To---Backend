package com.whoami.launch.order.orders.repository;

import com.whoami.launch.order.orders.entity.DailyBusinessAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyBusinessAnalyticsRepository
        extends JpaRepository<DailyBusinessAnalytics, Long> {

    Optional<DailyBusinessAnalytics>
    findByBusinessIdAndAnalyticsDate(
            String businessId,
            LocalDate date);
}