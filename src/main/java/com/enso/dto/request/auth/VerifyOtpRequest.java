package com.enso.dto.request.auth;

import com.enso.validation.IranianMobile;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyOtpRequest {

    @NotBlank(message = "Phone number is required")
    @IranianMobile
    private String phone;

    @NotBlank(message = "OTP is required")
    @Pattern(
            regexp = "^\\d{6}$",
            message = "OTP must be exactly 6 digit"
    )
    private String otp;
}
