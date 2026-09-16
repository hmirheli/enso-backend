package com.enso.dto.gateway.zarinpal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ZarinpalVerifyRequest {

    @JsonProperty("merchant_id")
    private String merchantId;
    private Long amount;
    private String authority;
}
