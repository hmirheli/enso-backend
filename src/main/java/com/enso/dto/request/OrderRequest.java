package com.enso.dto.request;

import com.enso.validation.IranianMobile;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OrderRequest {

    @NotBlank(message = "Recipient name is required")
    private String recipientName;

    @NotBlank(message = "Recipient mobile is required")
    @IranianMobile
    private String recipientMobile;

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;
}
