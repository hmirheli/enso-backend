package com.enso.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class IranianMobileValidator implements ConstraintValidator<IranianMobile, String> {

    private static final Pattern PATTERN = Pattern.compile("^09\\d{9}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext ctv) {

        return value == null || PATTERN.matcher(value).matches();
    }
}
