package com.tennisclub.reservations.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDto(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fieldErrors
) {

    public static ErrorResponseDto of(HttpStatus status, String message, String path) {
        return new ErrorResponseDto(Instant.now(), status.value(), status.getReasonPhrase(), message, path, null);
    }

    public static ErrorResponseDto validation(String message, String path, Map<String, String> fieldErrors) {
        var status = HttpStatus.BAD_REQUEST;
        return new ErrorResponseDto(Instant.now(), status.value(), status.getReasonPhrase(), message, path, fieldErrors);
    }
}
