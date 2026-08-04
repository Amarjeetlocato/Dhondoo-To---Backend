  package com.whoami.launch.order.cart.service.impl;

import com.whoami.launch.order.cart.dto.AddToCartRequest;
import com.whoami.launch.order.cart.dto.CartResponse;
import com.whoami.launch.order.cart.dto.UpdateCartRequest;
import com.whoami.launch.order.cart.entity.Cart;
import com.whoami.launch.order.cart.repository.CartRepository;
import com.whoami.launch.order.cart.service.CartService;
import com.whoami.launch.order.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    @Transactional
    public CartResponse addToCart(String userId, AddToCartRequest req) {
        Cart cart = cartRepository.findByUserIdAndProductId(userId, req.getProductId())
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUserId(userId);
                    c.setShopId(req.getShopId());
                    c.setProductId(req.getProductId());
                    c.setProductNameSnapshot(req.getProductName());
                    c.setImageSnapshot(req.getImageUrl());
                    c.setPriceSnapshot(req.getPrice());
                    c.setQuantity(0);
                    return c;
                });

        if (cart.getShopId() == null) {
            cart.setShopId(req.getShopId());
        }
        cart.setQuantity(cart.getQuantity() + req.getQuantity());
        Cart saved = cartRepository.save(cart);
        return toResponse(saved);
    }

    @Override
    public List<CartResponse> getCart(String userId) {
        List<Cart> list = cartRepository.findByUserId(userId);
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CartResponse updateQuantity(String userId, UpdateCartRequest req) {
        Cart c = cartRepository.findByCartIdAndUserId(req.getId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        c.setQuantity(req.getQuantity());
        Cart saved = cartRepository.save(c);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void removeItem(String userId, Long id) {
        Cart cart = cartRepository.findByCartIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
        cartRepository.delete(cart);
    }

    @Override
    @Transactional
    public void clearCart(String userId) {
        cartRepository.deleteByUserId(userId);
    }

    private CartResponse toResponse(Cart c) {
        CartResponse r = new CartResponse();
        r.setId(c.getCartId());
        r.setUserId(c.getUserId());
        r.setShopId(c.getShopId());
        r.setProductId(c.getProductId());
        r.setProductNameSnapshot(c.getProductNameSnapshot());
        r.setImageSnapshot(c.getImageSnapshot());
        r.setPriceSnapshot(c.getPriceSnapshot());
        r.setQuantity(c.getQuantity());
        r.setCreatedAt(c.getCreatedAt());
        r.setUpdatedAt(c.getUpdatedAt());
        return r;
    }

}
