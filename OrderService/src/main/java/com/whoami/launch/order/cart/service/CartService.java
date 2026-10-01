package com.whoami.launch.order.cart.service;

import com.whoami.launch.order.cart.dto.AddToCartRequest;
import com.whoami.launch.order.cart.dto.CartResponse;
import com.whoami.launch.order.cart.dto.UpdateCartRequest;

import java.util.List;

public interface CartService {

    CartResponse addToCart(
            String customerId,
            AddToCartRequest req);

    List<CartResponse> getCart(
            String customerId);

    CartResponse updateQuantity(
            String customerId,
            UpdateCartRequest req);

    void removeItem(
            String customerId,
            String cartId);

    void clearCart(
            String customerId);
}