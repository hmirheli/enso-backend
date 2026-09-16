package com.enso.dto.gateway.zarinpal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ZarinpalVerifyData {

    private Integer code;
    private String message;

    @JsonProperty("card_hash")
    private String cardHash;

    @JsonProperty("card_pan")
    private String cardPan;

    @JsonProperty("ref_id")
    private Long refId;

    private Long fee;

    @JsonProperty("fee_type")
    private String feeType;
}
