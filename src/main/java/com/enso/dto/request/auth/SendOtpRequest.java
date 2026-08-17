package com.enso.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SendOtpRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^09\\d{9}$",
            message = "Invalid Iranian mobile number"
    )
    private String phone;
}
