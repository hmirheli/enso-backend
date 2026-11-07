package com.enso.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IranianMobileValidator.class)
@Documented
public @interface IranianMobile {
    String message() default "Invalid Iranian mobile number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
