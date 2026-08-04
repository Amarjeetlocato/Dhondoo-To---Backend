package com.whoami.launch.order.cart.controller;

import com.whoami.launch.order.cart.dto.AddToCartRequest;
import com.whoami.launch.order.cart.dto.CartResponse;
import com.whoami.launch.order.cart.dto.UpdateCartRequest;
import com.whoami.launch.order.cart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody AddToCartRequest req,
                                                    @RequestParam String customerId) {
        CartResponse res = cartService.addToCart(customerId, req);
        return ResponseEntity.ok(res);
    }

    @GetMapping
    public ResponseEntity<List<CartResponse>> getCart(@RequestParam String customerId) {
        return ResponseEntity.ok(cartService.getCart(customerId));
    }

    @PutMapping("/update")
    public ResponseEntity<CartResponse> update(@Valid @RequestBody UpdateCartRequest req,
                                                @RequestParam String customerId) {
        return ResponseEntity.ok(cartService.updateQuantity(customerId, req));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id,
                                       @RequestParam String customerId) {
        cartService.removeItem(customerId, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clear(@RequestParam String customerId) {
        cartService.clearCart(customerId);
        return ResponseEntity.noContent().build();
    }

}
