package com.tennisclub.reservations.validator.annotation;

import com.tennisclub.reservations.validator.PhoneNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validates that a value is a valid phone number.
 */
@Documented
@Retention(RUNTIME)
@Target({ FIELD, ANNOTATION_TYPE, TYPE })
@Constraint(validatedBy = PhoneNumberValidator.class)
public @interface PhoneNumber {

    /**
     * Validation error message.
     */
    String message() default "Invalid phone number";

    /**
     * Validation groups.
     */
    Class<?>[] groups() default {};

    /**
     * Validation payload.
     */
    Class<? extends Payload>[] payload() default {};
}
