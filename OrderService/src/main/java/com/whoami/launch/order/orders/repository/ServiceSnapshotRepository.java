package com.whoami.launch.order.orders.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.whoami.launch.order.orders.entity.ServiceSnapshot;

public interface ServiceSnapshotRepository
        extends JpaRepository<ServiceSnapshot, String> {
}