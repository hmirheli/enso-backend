package com.enso.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class OrderRequest {

    @NotBlank(message = "Recipient name is required")
    private String recipientName;

    @NotBlank(message = "Recipient mobile is required")
    @Pattern(
            regexp = "^09\\d{9}$",
            message = "Invalid Iranian mobile number"
    )
    private String recipientMobile;

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
}
