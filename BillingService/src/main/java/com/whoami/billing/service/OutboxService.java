package com.whoami.billing.service;

import com.whoami.billing.domain.entity.OutboxEvent;

public interface OutboxService {

    OutboxEvent save(OutboxEvent event);
}