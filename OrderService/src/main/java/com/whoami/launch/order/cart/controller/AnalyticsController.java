package com.whoami.launch.order.cart.controller;

import com.whoami.launch.order.orders.dto.AnalyticsSummaryDto;
import com.whoami.launch.order.orders.dto.SalesItemDto;
import com.whoami.launch.order.orders.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary/{businessId}")
    public AnalyticsSummaryDto getTodaySummary(
            @PathVariable String businessId) {

        return analyticsService.getTodaySummary(businessId);
    }

    @GetMapping("/sales/{businessId}")
    public List<SalesItemDto> getSales(
            @PathVariable String businessId,
            @RequestParam LocalDate date) {

        return analyticsService.getSalesByDate(
                businessId,
                date);
    }
}
