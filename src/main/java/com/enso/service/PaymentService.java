package com.enso.service;

import com.enso.dto.gateway.payment.PaymentGatewayResponse;
import com.enso.dto.gateway.payment.PaymentVerificationResponse;
import com.enso.dto.request.CreatePaymentRequest;
import com.enso.dto.response.PaymentResponse;
import com.enso.entity.OrderEntity;
import com.enso.entity.PaymentEntity;
import com.enso.entity.UserEntity;
import com.enso.enums.OrderStatus;
import com.enso.enums.PaymentStatus;
import com.enso.exception.BadRequestException;
import com.enso.exception.ResourceNotFoundException;
import com.enso.mapper.PaymentMapper;
import com.enso.payment.PaymentGateway;
import com.enso.payment.gateway.PaymentGatewayResolver;
import com.enso.repository.OrderRepository;
import com.enso.repository.PaymentRepository;
import com.enso.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentGatewayResolver paymentGatewayResolver;

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        UserEntity currentUser = getCurrentUser();
        OrderEntity order = orderRepository.findById(request.getOrderId()).orElseThrow(
                () -> new ResourceNotFoundException("Order not found", request.getOrderId()));

        validateOrderOwnership(order, currentUser);
        validateOrderForPayment(order);

        PaymentEntity payment = PaymentEntity.builder()
                .order(order)
                .amount(order.getTotalAmount())
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .build();

        payment = paymentRepository.save(payment);
        PaymentGateway paymentGateway = paymentGatewayResolver.resolve(request.getPaymentMethod());

        PaymentGatewayResponse gatewayResponse = paymentGateway.requestPayment(payment);

        payment.setAuthority(gatewayResponse.getAuthority());
        paymentRepository.save(payment);

        return paymentMapper.toPaymentResponse(payment, gatewayResponse.getPaymentUrl());
    }

    @Transactional
    public void verifyPayment(String authority) {

        PaymentEntity payment = paymentRepository.findByAuthority(authority).orElseThrow(
                () -> new ResourceNotFoundException("Payment not found with authority: ", authority));

        if (PaymentStatus.SUCCESS.equals(payment.getStatus())) {
            return;
        }

        PaymentGateway paymentGateway = paymentGatewayResolver.resolve(payment.getPaymentMethod());

        PaymentVerificationResponse verificationResponse = paymentGateway.verifyPayment(payment, authority);

        if (!verificationResponse.isSuccessful()) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            return;
        }

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setReferenceId(verificationResponse.getReferenceId());
        payment.getOrder().setStatus(OrderStatus.PAID);
        paymentRepository.save(payment);
    }

    private UserEntity getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String mobileNumber = authentication.getName();

        return userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void validateOrderOwnership(OrderEntity order, UserEntity user) {

        if (!order.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("You are not allowed to pay for this order");
        }
    }

    private void validateOrderForPayment(OrderEntity order) {

        if (order.getStatus().equals(OrderStatus.CANCELLED)) {
            throw new BadRequestException("Cancelled order cannot be paid");
        }
    }

    public PaymentStatus getPaymentStatus(String authority) {
        return paymentRepository.findByAuthority(authority)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payment not found with authority: " + authority))
                .getStatus();
    }
}
