package com.whoami.launch.order.orders.service.impl;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.locato.constants.events.product.ProductCreatedEvent;
import com.locato.constants.events.product.ProductDeletedEvent;
import com.locato.constants.events.product.ProductUpdatedEvent;
import com.locato.constants.events.service.ServiceCreatedEvent;
import com.locato.constants.events.service.ServiceDeletedEvent;
import com.locato.constants.events.service.ServiceUpdatedEvent;

import com.whoami.launch.order.orders.entity.ProductSnapshot;
import com.whoami.launch.order.orders.entity.ServiceSnapshot;
import com.whoami.launch.order.orders.repository.ProductSnapshotRepository;
import com.whoami.launch.order.orders.repository.ServiceSnapshotRepository;
import com.whoami.launch.order.orders.service.OrderSyncService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderSyncServiceImpl
        implements OrderSyncService {

    private final ProductSnapshotRepository productRepository;

    private final ServiceSnapshotRepository serviceRepository;

    @Override
    public void handleProductCreated(
            ProductCreatedEvent event) {

        ProductSnapshot product =
                ProductSnapshot.builder()
                        .productId(event.getProductId())
                        .businessId(event.getBusinessId())
                        .businessName(event.getBusinessName())
                        .userId(event.getUserId())
                        .productName(event.getProductName())
                        .productPrice(
                                event.getProductPrice() != null
                                        ? BigDecimal.valueOf(
                                                event.getProductPrice())
                                        : null
                        )
                        .build();

        productRepository.save(product);
    }

    @Override
    public void handleProductUpdated(
            ProductUpdatedEvent event) {

        Optional<ProductSnapshot> optional =
                productRepository.findByProductId(
                        event.getProductId()
                );

        if (optional.isEmpty()) {
            return;
        }

        ProductSnapshot product =
                optional.get();

        product.setProductName(
                event.getProductName()
        );

        if (event.getProductPrice() != null) {
            product.setProductPrice(
                    BigDecimal.valueOf(
                            event.getProductPrice())
            );
        }

        productRepository.save(product);
    }

    @Override
    public void handleProductDeleted(
            ProductDeletedEvent event) {

        productRepository.deleteByProductId(
                event.getProductId()
        );
    }

    @Override
    public void handleServiceCreated(
            ServiceCreatedEvent event) {

        ServiceSnapshot service =
                ServiceSnapshot.builder()
                        .serviceId(event.getServiceId())
                        .businessId(event.getBusinessId())
                        .businessName(event.getBusinessName())
                        .userId(event.getUserId())
                        .serviceName(event.getServiceName())
                        .price(
                                event.getPrice() != null
                                        ? BigDecimal.valueOf(
                                                event.getPrice())
                                        : null
                        )
                        .build();

        serviceRepository.save(service);
    }

    @Override
    public void handleServiceUpdated(
            ServiceUpdatedEvent event) {

        Optional<ServiceSnapshot> optional =
                serviceRepository.findByServiceId(
                        event.getServiceId()
                );

        if (optional.isEmpty()) {
            return;
        }

        ServiceSnapshot service =
                optional.get();

        service.setServiceName(
                event.getServiceName()
        );

        if (event.getPrice() != null) {
            service.setPrice(
                    BigDecimal.valueOf(
                            event.getPrice())
            );
        }

        serviceRepository.save(service);
    }

    @Override
    public void handleServiceDeleted(
            ServiceDeletedEvent event) {

        serviceRepository.deleteByServiceId(
                event.getServiceId()
        );
    }
}