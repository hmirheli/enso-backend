package com.enso.dto.gateway.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentGatewayResponse {

    private String authority;
    private String paymentUrl;
}
