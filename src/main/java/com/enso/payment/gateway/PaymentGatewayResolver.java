package com.enso.payment.gateway;

import com.enso.enums.PaymentMethod;
import com.enso.exception.BadRequestException;
import com.enso.payment.PaymentGateway;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PaymentGatewayResolver {

    private final Map<PaymentMethod, PaymentGateway> gatewayMap;

    public PaymentGatewayResolver(List<PaymentGateway> gateways) {
        this.gatewayMap = gateways.stream()
                .collect(Collectors.toMap(
                        PaymentGateway::getSupportedMethod,
                        Function.identity()));
    }

    public PaymentGateway resolve(PaymentMethod paymentMethod) {
        PaymentGateway paymentGateway = gatewayMap.get(paymentMethod);
        if (paymentGateway == null) {
            throw new BadRequestException("Unsupported payment method: " + paymentMethod);
        }

        return paymentGateway;
    }
}
