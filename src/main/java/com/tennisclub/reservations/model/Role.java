package com.tennisclub.reservations.model;

import lombok.Getter;

/**
 * Represents an application user role.
 */
@Getter
public enum Role {
    USER("USER"),
    ADMIN("ADMIN");

    private final String value;

    Role(String value) {
        this.value = value;
    }
}
