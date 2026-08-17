package com.enso.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyOtpRequest {

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^09\\d{9}$",
            message = "Invalid Iranian mobile number"
    )
    private String phone;

    @NotBlank(message = "OTP is required")
    @Pattern(
            regexp = "^\\d{6}$",
            message = "OTP must be exactly 6 digit"
    )
    private String otp;
}
