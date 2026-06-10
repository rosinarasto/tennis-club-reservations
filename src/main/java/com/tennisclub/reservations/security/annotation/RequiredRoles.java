package com.tennisclub.reservations.security.annotation;

import com.tennisclub.reservations.model.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Restricts access to a controller or method to users with at least one of the given roles.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredRoles {

    /**
     * Roles allowed to access the annotated controller or method.
     *
     * @return allowed roles.
     */
    Role[] value();
}
