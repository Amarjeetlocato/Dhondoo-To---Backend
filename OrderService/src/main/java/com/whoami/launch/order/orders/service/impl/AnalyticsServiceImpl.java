package com.whoami.launch.order.orders.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.whoami.launch.order.orders.dto.AnalyticsSummaryDto;
import com.whoami.launch.order.orders.dto.SalesItemDto;
import com.whoami.launch.order.orders.entity.DailyShopAnalytics;
import com.whoami.launch.order.orders.repository.AnalyticsTransactionRepository;
import com.whoami.launch.order.orders.repository.DailyShopAnalyticsRepository;
import com.whoami.launch.order.orders.service.AnalyticsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl
        implements AnalyticsService {

    private final DailyShopAnalyticsRepository analyticsRepository;

    private final AnalyticsTransactionRepository transactionRepository;

    @Override
    public AnalyticsSummaryDto getTodaySummary(
            String shopId
    ) {

        DailyShopAnalytics analytics =
                analyticsRepository
                        .findByShopIdAndAnalyticsDate(
                                shopId,
                                LocalDate.now()
                        )
                        .orElse(new DailyShopAnalytics());

        return AnalyticsSummaryDto.builder()
                .totalOrders(
                        analytics.getTotalOrders()
                )
                .totalProductsSold(
                        analytics.getTotalProductsSold()
                )
                .totalServicesBooked(
                        analytics.getTotalServicesBooked()
                )
                .totalRevenue(
                        analytics.getTotalRevenue()
                )
                .build();
    }

    @Override
    public List<SalesItemDto> getSalesByDate(
            String shopId,
            LocalDate date
    ) {

        LocalDateTime start =
                date.atStartOfDay();

        LocalDateTime end =
                date.atTime(23,59,59);

        return transactionRepository
                .findByShopIdAndSoldAtBetween(
                        shopId,
                        start,
                        end
                )
                .stream()
                .map(tx -> SalesItemDto.builder()
                        .itemName(tx.getItemName())
                        .itemType(tx.getItemType())
                        .quantity(tx.getQuantity())
                        .amount(tx.getTotalAmount())
                        .soldAt(tx.getSoldAt())
                        .build())
                .toList();
    }
}