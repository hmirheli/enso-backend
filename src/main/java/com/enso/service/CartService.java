package com.enso.service;

import com.enso.dto.request.CartItemRequest;
import com.enso.dto.response.CartResponse;
import com.enso.entity.CartEntity;
import com.enso.entity.CartItemEntity;
import com.enso.entity.ProductEntity;
import com.enso.entity.UserEntity;
import com.enso.exception.BadRequestException;
import com.enso.exception.ResourceNotFoundException;
import com.enso.mapper.CartMapper;
import com.enso.repository.CartRepository;
import com.enso.repository.ProductRepository;
import com.enso.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public CartResponse getMyCart() {
        CartEntity cartEntity = getCurrentUserCart();
        return cartMapper.toCartResponce(cartEntity);
    }

    @Transactional
    public CartResponse addItem(CartItemRequest request) {

        CartEntity cart = getCurrentUserCart();
        ProductEntity product = productRepository.findByIdAndDeletedFalse(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", request.getProductId()));

        CartItemEntity existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        int finalQuantity = request.getQuantity();
        if (existingItem != null) {
            finalQuantity += existingItem.getQuantity();
        }

        if (product.getStock() < finalQuantity) {
            throw new BadRequestException("Not enough stock");
        }

        if (existingItem != null) {
            existingItem.setQuantity(finalQuantity);
        } else {
            CartItemEntity newItem = CartItemEntity.builder()
                    .product(product)
                    .quantity(request.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();

            cart.addItem(newItem);
        }

        return cartMapper.toCartResponce(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse updateItem(Long productId, Integer quantity) {

        if (quantity == null || quantity < 1) {
            throw new BadRequestException("Quantity must be at least 1");
        }

        CartEntity cart = getCurrentUserCart();
        CartItemEntity cartItem = findCartItem(cart, productId);

        if (cartItem.getProduct().getStock() < quantity) {
            throw new BadRequestException("Insufficient stock. Available: " + cartItem.getProduct().getStock());
        }

        cartItem.setQuantity(quantity);
        return cartMapper.toCartResponce(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse removeItem(Long productId) {

        CartEntity cart = getCurrentUserCart();
        CartItemEntity cartItem = findCartItem(cart, productId);
        cart.removeItem(cartItem);
        return cartMapper.toCartResponce(cartRepository.save(cart));
    }

    @Transactional
    public void clearCart() {
        CartEntity cart = getCurrentUserCart();
        cart.getItems().clear();
    }

    private CartEntity getCurrentUserCart() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String mobileNumber = authentication.getName();

        UserEntity user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for current user"));
    }

    private CartItemEntity findCartItem(CartEntity cart, Long productId) {

        return cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Cart item", productId));
    }
}
