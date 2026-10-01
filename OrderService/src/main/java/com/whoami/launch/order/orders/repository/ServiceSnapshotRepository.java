package com.whoami.launch.order.orders.repository;

import com.whoami.launch.order.orders.entity.ServiceSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceSnapshotRepository
        extends JpaRepository<ServiceSnapshot, Long> {

    Optional<ServiceSnapshot> findByServiceId(
            String serviceId);

    void deleteByServiceId(
            String serviceId);
}