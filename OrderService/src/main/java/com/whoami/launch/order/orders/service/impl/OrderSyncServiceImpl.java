package com.whoami.launch.order.orders.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.locato.dto.ProductCreatedEvent;
import com.locato.dto.ProductDeletedEvent;
import com.locato.dto.ProductUpdatedEvent;
import com.locato.dto.ServiceCreatedEvent;
import com.locato.dto.ServiceDeletedEvent;
import com.locato.dto.ServiceUpdatedEvent;
import com.whoami.launch.order.orders.entity.ProductSnapshot;
import com.whoami.launch.order.orders.entity.ServiceSnapshot;
import com.whoami.launch.order.orders.repository.ProductSnapshotRepository;
import com.whoami.launch.order.orders.repository.ServiceSnapshotRepository;
import com.whoami.launch.order.orders.service.OrderSyncService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderSyncServiceImpl implements OrderSyncService {

    private final ProductSnapshotRepository productRepository;
    private final ServiceSnapshotRepository serviceRepository;

    @Override
    public void handleProductCreated(ProductCreatedEvent event) {

        ProductSnapshot product = ProductSnapshot.builder()
                .productId(event.getProductId())
                .shopId(event.getShopId())
                .shopName(event.getShopName())
                .userId(event.getUserId())
                .productName(event.getProductName())
                .productPrice(event.getProductPrice())
                .build();

        productRepository.save(product);
    }

    @Override
    public void handleProductUpdated(ProductUpdatedEvent event) {

        Optional<ProductSnapshot> optional =
                productRepository.findById(event.getProductId());

        if (optional.isEmpty()) {
            return;
        }

        ProductSnapshot product = optional.get();

        // update only fields present in ProductUpdatedEvent
        product.setProductName(event.getProductName());

        productRepository.save(product);
    }

    @Override
    public void handleProductDeleted(ProductDeletedEvent event) {

        productRepository.deleteById(event.getProductId());
    }

    @Override
    public void handleServiceCreated(ServiceCreatedEvent event) {

        ServiceSnapshot service = ServiceSnapshot.builder()
                .serviceId(event.getServiceId())
                .shopId(event.getShopId())
                .shopName(event.getShopName())
                .userId(event.getUserId())
                .serviceName(event.getServiceName())
                .price(event.getPrice())
                .build();

        serviceRepository.save(service);
    }

    @Override
    public void handleServiceUpdated(ServiceUpdatedEvent event) {

        Optional<ServiceSnapshot> optional =
                serviceRepository.findById(event.getServiceId());

        if (optional.isEmpty()) {
            return;
        }

        ServiceSnapshot service = optional.get();

        service.setServiceName(event.getServiceName());

        serviceRepository.save(service);
    }

    @Override
    public void handleServiceDeleted(ServiceDeletedEvent event) {

        serviceRepository.deleteById(event.getServiceId());
    }
}