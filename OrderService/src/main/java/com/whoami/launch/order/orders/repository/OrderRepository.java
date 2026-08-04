package com.whoami.launch.order.orders.repository;

import com.whoami.launch.order.orders.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerId(String customerId);

    List<Order> findByShopId(String shopId);

    Optional<Order> findByOrderId(String orderId);

}
