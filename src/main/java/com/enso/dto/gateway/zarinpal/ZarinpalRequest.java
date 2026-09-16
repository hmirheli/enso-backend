package com.enso.dto.gateway.zarinpal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ZarinpalRequest {

    @JsonProperty("merchant_id")
    private String merchantId;

    private Long amount;

    @JsonProperty("callback_url")
    private String callbackUrl;

    private String description;

    private Map<String, String> metadata;
}
