package com.enso.dto.response;

import com.enso.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {

    private Long id;
    private Long userId;
    private List<OrderItemResponse> orderItems;
    private String recipientName;
    private String recipientMobile;
    private String shippingAddress;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String paymentReference;
    private String trackingCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
