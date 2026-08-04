package com.whoami.launch.order.orders.service;


import java.time.LocalDate;
import java.util.List;

import com.whoami.launch.order.orders.dto.AnalyticsSummaryDto;
import com.whoami.launch.order.orders.dto.SalesItemDto;

public interface AnalyticsService {

    AnalyticsSummaryDto getTodaySummary(
            String shopId
    );

    List<SalesItemDto> getSalesByDate(
            String shopId,
            LocalDate date
    );
}