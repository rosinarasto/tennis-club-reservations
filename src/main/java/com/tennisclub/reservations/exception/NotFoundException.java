package com.tennisclub.reservations.exception;

/**
 * Signals that a requested application resource could not be found.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Creates a new not-found exception.
     *
     * @param message detail message.
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Creates a new not-found exception with a root cause.
     *
     * @param message detail message.
     * @param cause root cause.
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
