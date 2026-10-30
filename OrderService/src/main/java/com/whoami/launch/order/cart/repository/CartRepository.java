package com.whoami.launch.order.cart.repository;

import com.whoami.launch.order.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.customerId = :customerId")
    void deleteByCustomerId(@Param("customerId") String customerId);
}
