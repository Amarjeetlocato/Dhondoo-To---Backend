package com.whoami.launch.order.cart.service.impl;

import com.whoami.launch.order.cart.dto.AddToCartRequest;
import com.whoami.launch.order.cart.dto.CartResponse;
import com.whoami.launch.order.cart.dto.UpdateCartRequest;
import com.whoami.launch.order.cart.entity.Cart;
import com.whoami.launch.order.cart.repository.CartRepository;
import com.whoami.launch.order.cart.service.CartService;
import com.whoami.launch.order.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    @Override
    @Transactional
    public CartResponse addToCart(
            String customerId,
            AddToCartRequest req) {

        Cart cart = cartRepository
                .findByCustomerIdAndProductId(
                        customerId,
                        req.getProductId()
                )
                .orElseGet(() -> {

                    Cart c = new Cart();

                    c.setCustomerId(customerId);
                    c.setBusinessId(req.getBusinessId());
                    c.setProductId(req.getProductId());

                    c.setProductNameSnapshot(
                            req.getProductName()
                    );

                    c.setImageSnapshot(
                            req.getImageUrl()
                    );

                    c.setPriceSnapshot(
                            req.getPrice()
                    );

                    c.setQuantity(0);

                    return c;
                });

        if (cart.getBusinessId() == null) {
            cart.setBusinessId(req.getBusinessId());
        }

        cart.setQuantity(
                cart.getQuantity() + req.getQuantity()
        );

        Cart saved =
                cartRepository.save(cart);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CartResponse> getCart(
            String customerId) {

        return cartRepository
                .findByCustomerId(customerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CartResponse updateQuantity(
            String customerId,
            UpdateCartRequest req) {

        Cart cart = cartRepository
                .findByCartIdAndCustomerId(
                        req.getId(),
                        customerId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart item not found"
                        )
                );

        cart.setQuantity(
                req.getQuantity()
        );

        Cart saved =
                cartRepository.save(cart);

        return toResponse(saved);
    }

    @Override
    @Transactional
    public void removeItem(
            String customerId,
            String cartId) {

        Cart cart = cartRepository
                .findByCartIdAndCustomerId(
                        cartId,
                        customerId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cart item not found"
                        )
                );

        cartRepository.delete(cart);
    }

    @Override
    @Transactional
    public void clearCart(
            String customerId) {

        cartRepository.deleteByCustomerId(
                customerId
        );
    }

    private CartResponse toResponse(
            Cart cart) {

        CartResponse response =
                new CartResponse();

        response.setCartId(
                cart.getCartId()
        );

        response.setCustomerId(
                cart.getCustomerId()
        );

        response.setBusinessId(
                cart.getBusinessId()
        );

        response.setProductId(
                cart.getProductId()
        );

        response.setProductNameSnapshot(
                cart.getProductNameSnapshot()
        );

        response.setImageSnapshot(
                cart.getImageSnapshot()
        );

        response.setBusinessNameSnapshot(
                cart.getBusinessNameSnapshot()
        );

        response.setBusinessLogoSnapshot(
                cart.getBusinessLogoSnapshot()
        );

        response.setPriceSnapshot(
                cart.getPriceSnapshot()
        );

        response.setQuantity(
                cart.getQuantity()
        );

        response.setCreatedAt(
                cart.getCreatedAt()
        );

        response.setUpdatedAt(
                cart.getUpdatedAt()
        );

        return response;
    }
}