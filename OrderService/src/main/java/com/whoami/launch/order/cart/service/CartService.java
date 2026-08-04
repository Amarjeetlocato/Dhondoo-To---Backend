package com.whoami.launch.order.cart.service;

import com.whoami.launch.order.cart.dto.AddToCartRequest;
import com.whoami.launch.order.cart.dto.CartResponse;
import com.whoami.launch.order.cart.dto.UpdateCartRequest;

import java.util.List;

public interface CartService {

    CartResponse addToCart(String userId, AddToCartRequest req);

    List<CartResponse> getCart(String userId);

    CartResponse updateQuantity(String userId, UpdateCartRequest req);

    void removeItem(String userId, Long id);

    void clearCart(String userId);

}
