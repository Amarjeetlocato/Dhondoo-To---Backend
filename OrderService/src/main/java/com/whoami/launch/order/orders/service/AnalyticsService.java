package com.whoami.launch.order.orders.service;

import com.whoami.launch.order.orders.dto.AnalyticsSummaryDto;
import com.whoami.launch.order.orders.dto.SalesItemDto;

import java.time.LocalDate;
import java.util.List;

public interface AnalyticsService {

    AnalyticsSummaryDto getTodaySummary(
            String businessId
    );

    List<SalesItemDto> getSalesByDate(
            String businessId,
            LocalDate date
    );
}