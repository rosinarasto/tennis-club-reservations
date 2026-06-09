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

@Documented
@Retention(RUNTIME)
@Target({ANNOTATION_TYPE, TYPE})
@Constraint(validatedBy = AvailableDateValidator.class)
public @interface AvailableDate {

    String message() default "Reservation date is not available";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String courtField() default "court";
    String fromField() default "from";
    String toField() default "to";

    @Target({ANNOTATION_TYPE, TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        AvailableDate[] value();
    }
}
