package com.whoami.launch.order.orders.repository;

import com.whoami.launch.order.orders.entity.ProductSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductSnapshotRepository
        extends JpaRepository<ProductSnapshot, Long> {

    Optional<ProductSnapshot> findByProductId(
            String productId);

    void deleteByProductId(
            String productId);
}