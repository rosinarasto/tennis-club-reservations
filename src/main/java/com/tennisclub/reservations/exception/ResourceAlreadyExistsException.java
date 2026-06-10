package com.tennisclub.reservations.exception;

/**
 * Signals that a resource cannot be created because an equivalent resource already exists.
 */
public class ResourceAlreadyExistsException extends RuntimeException {

    /**
     * Creates a new resource-already-exists exception.
     *
     * @param message detail message.
     */
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * Creates a new resource-already-exists exception with a root cause.
     *
     * @param message detail message.
     * @param cause root cause.
     */
    public ResourceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
