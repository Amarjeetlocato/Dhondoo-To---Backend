package com.whoami.launch.order.cart.repository;

import com.whoami.launch.order.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository
        extends JpaRepository<Cart, Long> {

    List<Cart> findByCustomerId(String customerId);

    Optional<Cart> findByCustomerIdAndProductId(
            String customerId,
            String productId);

    Optional<Cart> findByCartIdAndCustomerId(
            String cartId,
            String customerId);

    void deleteByCustomerId(String customerId);
}