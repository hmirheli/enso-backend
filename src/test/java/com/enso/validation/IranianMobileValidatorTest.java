package com.enso.validation;

import com.enso.dto.request.auth.SendOtpRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class IranianMobileValidatorTest {

    private final IranianMobileValidator validator = new IranianMobileValidator();

    @ParameterizedTest
    @ValueSource(strings = {"09121234567", "09000000000", "09999999999"})
    void isValid_withCorrectNumber_returnsTrue(String phone) {
        assertThat(validator.isValid(phone, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"9121234567", "0912123456", "091212345678",
            "09a21234567", "+989121234567", ""})
    void isValid_withWrongFormat_returnsFalse(String phone) {
        assertThat(validator.isValid(phone, null)).isFalse();
    }

    @Test
    void isValid_withNull_returnsTrue() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    void sendOtpRequest_withInvalidPhone_hasViolation() {
        SendOtpRequest otpRequest = new SendOtpRequest();
        otpRequest.setPhone("123");

        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Set<ConstraintViolation<SendOtpRequest>> violations = factory.getValidator().validate(otpRequest);
            assertThat(violations).hasSize(1);
            assertThat(violations.iterator().next().getMessage()).isEqualTo("Invalid Iranian mobile number");
        }
    }
}