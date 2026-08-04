package com.whoami.launch.order.orders.service;

import com.locato.dto.ProductCreatedEvent;
import com.locato.dto.ProductDeletedEvent;
import com.locato.dto.ProductUpdatedEvent;
import com.locato.dto.ServiceCreatedEvent;
import com.locato.dto.ServiceDeletedEvent;
import com.locato.dto.ServiceUpdatedEvent;

public interface OrderSyncService {

    void handleProductCreated(ProductCreatedEvent event);

    void handleProductUpdated(ProductUpdatedEvent event);

    void handleProductDeleted(ProductDeletedEvent event);

    void handleServiceCreated(ServiceCreatedEvent event);

    void handleServiceUpdated(ServiceUpdatedEvent event);

    void handleServiceDeleted(ServiceDeletedEvent event);
}