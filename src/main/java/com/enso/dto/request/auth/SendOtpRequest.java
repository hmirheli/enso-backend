package com.enso.dto.request.auth;

import com.enso.validation.IranianMobile;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendOtpRequest {

    @NotBlank(message = "Phone number is required")
    @IranianMobile
    private String phone;
}
