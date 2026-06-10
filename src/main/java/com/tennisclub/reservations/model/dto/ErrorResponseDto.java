package com.tennisclub.reservations.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

/**
 * Represents the unified JSON error response returned by REST and security exception handlers.
 *
 * @param timestamp time when the error response was created.
 * @param status HTTP status code.
 * @param error HTTP status reason phrase.
 * @param message human-readable error message.
 * @param path request path that caused the error.
 * @param fieldErrors validation errors keyed by field name.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDto(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors
) {

    /**
     * Creates a non-validation error response.
     *
     * @param status HTTP status.
     * @param message human-readable error message.
     * @param path request path.
     * @return error response.
     */
    public static ErrorResponseDto of(HttpStatus status, String message, String path) {
        return new ErrorResponseDto(Instant.now(), status.value(), status.getReasonPhrase(), message, path, null);
    }

    /**
     * Creates a validation error response with field-level errors.
     *
     * @param message human-readable error message.
     * @param path request path.
     * @param fieldErrors validation errors keyed by field name.
     * @return validation error response.
     */
    public static ErrorResponseDto validation(String message, String path, Map<String, String> fieldErrors) {
        var status = HttpStatus.BAD_REQUEST;
        return new ErrorResponseDto(Instant.now(), status.value(), status.getReasonPhrase(), message, path, fieldErrors);
    }
}
