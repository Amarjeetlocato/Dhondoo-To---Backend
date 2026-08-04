package com.whoami.launch.order.orders.repository;

import com.whoami.launch.order.orders.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(String orderId);

}
