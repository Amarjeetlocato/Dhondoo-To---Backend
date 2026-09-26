package com.whoami.billing.service.impl;

import com.whoami.billing.domain.entity.OutboxEvent;
import com.whoami.billing.repository.OutboxEventRepository;
import com.whoami.billing.service.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OutboxServiceImpl implements OutboxService {

    private final OutboxEventRepository repository;

    @Override
    public OutboxEvent save(OutboxEvent event) {
        return repository.save(event);
    }
}