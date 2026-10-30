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
            @RequestParam(required = false) String customerId) {

        if (customerId == null || customerId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        CartResponse response =
                cartService.addToCart(
                        customerId,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<CartResponse>> getCart(
            @RequestParam(required = false) String customerId) {

        if (customerId == null || customerId.isBlank()) {
            return ResponseEntity.ok(List.of());
        }

        return ResponseEntity.ok(
                cartService.getCart(customerId)
        );
    }

    @PutMapping("/update")
    public ResponseEntity<CartResponse> update(
            @Valid @RequestBody UpdateCartRequest request,
            @RequestParam(required = false) String customerId) {

        if (customerId == null || customerId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

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
            @RequestParam(required = false) String customerId) {

        if (customerId == null || customerId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        cartService.removeItem(
                customerId,
                cartId
        );

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clear(
            @RequestParam(required = false) String customerId) {

        if (customerId == null || customerId.isBlank()) {
            return ResponseEntity.noContent().build();
        }

        cartService.clearCart(customerId);

        return ResponseEntity.noContent().build();
    }
}
