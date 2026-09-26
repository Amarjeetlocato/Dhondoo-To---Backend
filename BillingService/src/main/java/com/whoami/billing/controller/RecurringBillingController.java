package com.whoami.billing.controller;

import com.whoami.billing.service.RecurringBillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/billing/recurring")
@RequiredArgsConstructor
public class RecurringBillingController {

    private final RecurringBillingService recurringBillingService;

    @PostMapping("/process")
    public ResponseEntity<String> processRecurringBilling() {

        log.info("Manual recurring billing processing started");

        try {

            recurringBillingService.processDueSubscriptions();

            log.info(
                    "Manual recurring billing processing completed"
            );

            return ResponseEntity.ok(
                    "Recurring billing processing completed"
            );

        } catch (Exception e) {

            log.error(
                    "Manual recurring billing processing failed",
                    e
            );

            throw e;
        }
    }

    @PostMapping("/process/test")
    public ResponseEntity<String> processRecurringBillingForDate(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        log.info(
                "Test recurring billing processing started: date={}",
                date
        );

        try {

            recurringBillingService
                    .processDueSubscriptions(date);

            log.info(
                    "Test recurring billing processing completed: date={}",
                    date
            );

            return ResponseEntity.ok(
                    "Recurring billing processing completed for date: "
                            + date
            );

        } catch (Exception e) {

            log.error(
                    "Test recurring billing processing failed: date={}",
                    date,
                    e
            );

            throw e;
        }
    }
}