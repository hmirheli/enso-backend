package com.enso.dto.gateway.zarinpal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ZarinpalData {

    private Integer code;
    private String message;
    private String authority;

    @JsonProperty("fee_type")
    private String feeType;

    private Long fee;
}
