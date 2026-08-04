package com.whoami.launch.order.cart.controller;


import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whoami.launch.order.orders.dto.AnalyticsSummaryDto;
import com.whoami.launch.order.orders.dto.SalesItemDto;
import com.whoami.launch.order.orders.service.AnalyticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary/{shopId}")
    public AnalyticsSummaryDto getTodaySummary(
            @PathVariable String shopId
    ) {
        return analyticsService
                .getTodaySummary(shopId);
    }

    @GetMapping("/sales/{shopId}")
    public List<SalesItemDto> getSales(
            @PathVariable String shopId,
            @RequestParam LocalDate date
    ) {
        return analyticsService
                .getSalesByDate(
                        shopId,
                        date
                );
    }
}