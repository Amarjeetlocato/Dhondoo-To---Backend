package com.whoami.launch.order.orders.service;

import com.locato.constants.events.product.ProductCreatedEvent;
import com.locato.constants.events.product.ProductDeletedEvent;
import com.locato.constants.events.product.ProductUpdatedEvent;
import com.locato.constants.events.service.ServiceCreatedEvent;
import com.locato.constants.events.service.ServiceDeletedEvent;
import com.locato.constants.events.service.ServiceUpdatedEvent;

public interface OrderSyncService {

    void handleProductCreated(
            ProductCreatedEvent event
    );

    void handleProductUpdated(
            ProductUpdatedEvent event
    );

    void handleProductDeleted(
            ProductDeletedEvent event
    );

    void handleServiceCreated(
            ServiceCreatedEvent event
    );

    void handleServiceUpdated(
            ServiceUpdatedEvent event
    );

    void handleServiceDeleted(
            ServiceDeletedEvent event
    );
}