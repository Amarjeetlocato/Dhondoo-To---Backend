package com.whoami.launch.order.orders.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whoami.launch.order.orders.entity.ProductSnapshot;

public interface ProductSnapshotRepository
        extends JpaRepository<ProductSnapshot, String> {
}