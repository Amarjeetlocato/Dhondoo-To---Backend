package com.whoami.launch.order.orders.repository;

import com.whoami.launch.order.orders.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository
        extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(String customerId);

    List<Order> findByBusinessId(String businessId);

    Optional<Order> findByOrderId(String orderId);
}