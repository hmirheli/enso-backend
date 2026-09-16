package com.enso.payment;

import com.enso.dto.gateway.payment.PaymentGatewayResponse;
import com.enso.dto.gateway.payment.PaymentVerificationResponse;
import com.enso.entity.PaymentEntity;
import com.enso.enums.PaymentMethod;

public interface PaymentGateway {
    PaymentMethod getSupportedMethod();

    PaymentGatewayResponse requestPayment(PaymentEntity payment);

    PaymentVerificationResponse verifyPayment(PaymentEntity payment, String authority);
}
