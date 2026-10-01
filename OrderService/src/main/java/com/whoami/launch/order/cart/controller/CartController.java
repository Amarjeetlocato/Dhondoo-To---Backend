package com.whoami.launch.order.cart.controller;

import com.whoami.launch.order.cart.dto.AddToCartRequest;
import com.whoami.launch.order.cart.dto.CartResponse;
import com.whoami.launch.order.cart.dto.UpdateCartRequest;
import com.whoami.launch.order.cart.service.CartService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            @RequestParam String customerId) {

        CartResponse response =
                cartService.addToCart(
                        customerId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CartResponse>> getCart(
            @RequestParam String customerId) {

        return ResponseEntity.ok(
                cartService.getCart(customerId)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<CartResponse> update(
            @Valid @RequestBody UpdateCartRequest request,
            @RequestParam String customerId) {

        return ResponseEntity.ok(
                cartService.updateQuantity(
                        customerId,
                        request
                )
        );
    }

    @DeleteMapping("/remove/{cartId}")
    public ResponseEntity<Void> remove(
            @PathVariable String cartId,
            @RequestParam String customerId) {

        cartService.removeItem(
                customerId,
                cartId
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clear(
            @RequestParam String customerId) {

        cartService.clearCart(customerId);

        return ResponseEntity.noContent().build();
    }
}