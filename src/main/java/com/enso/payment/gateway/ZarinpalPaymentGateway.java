package com.enso.payment.gateway;

import com.enso.config.ZarinpalConfig;
import com.enso.dto.gateway.payment.PaymentGatewayResponse;
import com.enso.dto.gateway.payment.PaymentVerificationResponse;
import com.enso.dto.gateway.zarinpal.*;
import com.enso.entity.PaymentEntity;
import com.enso.enums.PaymentMethod;
import com.enso.exception.PaymentGatewayException;
import com.enso.payment.PaymentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class ZarinpalPaymentGateway implements PaymentGateway {

    private final ZarinpalConfig zarinpalConfig;
    private final RestClient restClient;

    @Override
    public PaymentMethod getSupportedMethod() {
        return PaymentMethod.ZARINPAL;
    }

    @Override
    public PaymentGatewayResponse requestPayment(PaymentEntity payment) {

        ZarinpalRequest request = ZarinpalRequest.builder()
                .merchantId(zarinpalConfig.getMerchantId())
                .amount(payment.getAmount().longValueExact())
                .callbackUrl(zarinpalConfig.getCallbackUrl())
                .description("Payment for order " + payment.getOrder().getId())
                .build();

        try {
            ZarinpalResponse response = restClient.post()
                    .uri(zarinpalConfig.getRequestUrl())
                    .body(request)
                    .retrieve()
                    .body(ZarinpalResponse.class);

            if (response == null || response.getZarinpalData() == null) {
                throw new PaymentGatewayException(
                        "Invalid response received from Zarinpal"
                );
            }

            ZarinpalData data = response.getZarinpalData();

            if (data.getCode()  == null || data.getCode() != 100) {
                throw new PaymentGatewayException("Zarinpal payment request failed. Code: "
                        + data.getCode()
                        + ", Message: "
                        + data.getMessage()
                );
            }

            String paymentUrl = zarinpalConfig.getPaymentUrl() + data.getAuthority();

            return PaymentGatewayResponse.builder()
                    .authority(data.getAuthority())
                    .paymentUrl(paymentUrl)
                    .build();
        } catch (PaymentGatewayException e) {
            throw e;
        } catch (Exception e) {
            throw new PaymentGatewayException("Failed to communicate with Zarinpal", e);
        }
    }

    @Override
    public PaymentVerificationResponse verifyPayment(PaymentEntity payment, String authority) {

        ZarinpalVerifyRequest request = ZarinpalVerifyRequest.builder()
                .merchantId(zarinpalConfig.getMerchantId())
                .amount(payment.getAmount().longValueExact())
                .authority(authority)
                .build();

        try {
            ZarinpalVerifyResponse response = restClient.post()
                    .uri(zarinpalConfig.getVerifyUrl())
                    .body(request)
                    .retrieve()
                    .body(ZarinpalVerifyResponse.class);

            if (response == null || response.getData() == null) {
                throw new PaymentGatewayException("Invalid response received from Zarinpal");
            }

            ZarinpalVerifyData data = response.getData();
            boolean successful = data.getCode() != null && (data.getCode() == 100 || data.getCode() == 101);

            return PaymentVerificationResponse.builder()
                    .successful(successful)
                    .referenceId(data.getRefId() != null ? data.getRefId().toString() : null)
                    .build();
        }catch (PaymentGatewayException e) {
            throw e;
        }catch (Exception e) {
            throw new PaymentGatewayException("Failed to verify payment with Zarinpal", e);
        }
    }
}
