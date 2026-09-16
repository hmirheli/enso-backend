package com.enso.dto.gateway.payment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentVerificationResponse {

    private boolean successful;
    private String referenceId;
}
