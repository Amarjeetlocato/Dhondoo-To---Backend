package com.whoami.billing.service;

import java.time.LocalDate;

public interface RecurringBillingService {

    void processDueSubscriptions();

    void processDueSubscriptions(LocalDate asOfDate);
}