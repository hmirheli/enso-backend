package com.enso.controller;

import com.enso.dto.request.CartItemRequest;
import com.enso.dto.response.CartResponse;
import com.enso.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Authenticated user cart — add, update, remove items")
public class CartController {

    private final CartService cartService;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<CartResponse> getMyCart() {
        return ResponseEntity.ok(cartService.getMyCart());
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(@Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(request));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/items/{productId}")
    public ResponseEntity<CartResponse> updateItem(@PathVariable Long productId, @RequestBody Integer quantity) {
        return ResponseEntity.ok(cartService.updateItem(productId, quantity));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeItem(productId));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }
}
