package com.enso.dto.response;

import com.enso.enums.PaymentMethod;
import com.enso.enums.PaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {

    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String authority;
    private String referenceId;
    private String paymentUrl;
    private LocalDateTime createdAt;
}
