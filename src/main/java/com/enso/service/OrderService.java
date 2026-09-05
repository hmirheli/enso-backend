package com.enso.service;

import com.enso.dto.request.OrderRequest;
import com.enso.dto.response.OrderResponse;
import com.enso.entity.*;
import com.enso.enums.OrderStatus;
import com.enso.exception.BadRequestException;
import com.enso.exception.ResourceNotFoundException;
import com.enso.mapper.OrderMapper;
import com.enso.repository.OrderRepository;
import com.enso.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartService cartService;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {

        UserEntity user = getCurrentUser();
        CartEntity cart = cartService.getCartEntity(user);

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cannot create order from an empty car");
        }

        OrderEntity order = OrderEntity.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .recipientName(orderRequest.getRecipientName())
                .recipientMobile(orderRequest.getRecipientMobile())
                .shippingAddress(orderRequest.getShippingAddress())
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItemEntity cartItem : cart.getItems()) {
            ProductEntity product = cartItem.getProduct();

            if (product.getStock() < cartItem.getQuantity()) {
                throw new BadRequestException("Insufficient stock for product: " + cartItem.getProduct().getName());
            }
            product.setStock(product.getStock() - cartItem.getQuantity());

            OrderItemEntity orderItem = OrderItemEntity.builder()
                    .product(cartItem.getProduct())
                    .productName(cartItem.getProduct().getName())
                    .productImage(cartItem.getProduct().getImages().isEmpty()
                            ? null
                            : cartItem.getProduct().getImages().get(0).getImageUrl())
                    .unitPrice(cartItem.getUnitPrice())
                    .quantity(cartItem.getQuantity())
                    .build();

            order.addOrderItem(orderItem);
            totalAmount = totalAmount.add(orderItem.getSubtotal());
        }

        order.setTotalAmount(totalAmount);
        OrderEntity savedOrder = orderRepository.save(order);
        cartService.clearCart();
        return orderMapper.toOrderResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders() {
        UserEntity user = getCurrentUser();
        return orderRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(orderMapper::toOrderResponse)
                .toList();

    }

    @Transactional(readOnly = true)
    public OrderResponse getMyOrderById(Long orderId) {

        UserEntity user = getCurrentUser();
        OrderEntity order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        return orderMapper.toOrderResponse(order);
    }

    @Transactional
    public void cancelOrder(Long orderId) {

        UserEntity user = getCurrentUser();
        OrderEntity order = orderRepository.findByIdAndUser(orderId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("Only pending orders can be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
    }

    private UserEntity getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String mobileNumber = authentication.getName();
        return userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
    }

}
