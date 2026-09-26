package com.whoami.billing.scheduler;

import com.whoami.billing.service.RecurringBillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecurringBillingScheduler {

    private final RecurringBillingService recurringBillingService;

    @Scheduled(
            cron = "${billing.recurring.cron:0 0 2 * * *}",
            zone = "${billing.recurring.timezone:UTC}"
    )
    public void processRecurringBilling() {

        log.info("Starting recurring billing processing");

        recurringBillingService.processDueSubscriptions();

        log.info("Recurring billing processing completed");
    }
}