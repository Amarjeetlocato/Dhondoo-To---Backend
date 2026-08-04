package com.whoami.launch.order.cart.repository;

import com.whoami.launch.order.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    List<Cart> findByUserId(String userId);

    Optional<Cart> findByUserIdAndProductId(String userId, String productId);

    Optional<Cart> findByCartIdAndUserId(Long cartId, String userId);

    void deleteByUserId(String userId);

}
