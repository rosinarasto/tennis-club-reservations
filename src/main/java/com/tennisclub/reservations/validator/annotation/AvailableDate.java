package com.tennisclub.reservations.validator.annotation;

import com.tennisclub.reservations.validator.AvailableDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validates that a reservation interval is available for the selected court.
 */
@Documented
@Retention(RUNTIME)
@Target({ANNOTATION_TYPE, TYPE})
@Constraint(validatedBy = AvailableDateValidator.class)
public @interface AvailableDate {

    /**
     * Validation error message.
     */
    String message() default "Reservation date is not available";

    /**
     * Validation groups.
     */
    Class<?>[] groups() default {};

    /**
     * Validation payload.
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * Name of the field containing the court value.
     */
    String courtField() default "court";

    /**
     * Name of the field containing the reservation start.
     */
    String fromField() default "from";

    /**
     * Name of the field containing the reservation end.
     */
    String toField() default "to";

    /**
     * Allows declaring multiple {@link AvailableDate} constraints on the same element.
     */
    @Target({ANNOTATION_TYPE, TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {

        /**
         * Available date constraints.
         */
        AvailableDate[] value();
    }
}
