package com.enso.service.impl;

import com.enso.config.KavenegarConfig;
import com.enso.exception.SmsProviderException;
import com.enso.service.SmsService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
@RequiredArgsConstructor
public class KavenegarSmsService implements SmsService {

    @Qualifier("kavenegarRestClient")
    private final RestClient restClient;
    private final KavenegarConfig kavenegarConfig;

    @Override
    public void sendOtp(String phoneNumber, String code) {
        try{
        JsonNode response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/{apiKey}/verify/lookup.json")
                        .queryParam("receptor", phoneNumber)
                        .queryParam("token", code)
                        .queryParam("template", kavenegarConfig.getOtpTemplate())
                        .build(kavenegarConfig.getApiKey()))
                .retrieve().body(JsonNode.class);

        int status = response == null ? -1 : response.path("return").path("status").asInt(-1);

        if (status != 200) {

            throw new IllegalStateException("Kavenegar returned status " + status);
        }}catch (RestClientException | IllegalStateException ex){
            throw new SmsProviderException("ارسال پیامک از طریق کاوه‌نگار ناموفق بود.", ex);
        }


    }
}
