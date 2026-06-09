package com.tennisclub.reservations.config;

import com.tennisclub.reservations.exception.NotFoundException;
import com.tennisclub.reservations.exception.ResourceAlreadyExistsException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNullPointerException_returnsBadRequest() {
        var response = handler.handleNullPointerException(new NullPointerException("missing"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("missing");
    }

    @Test
    void handleResourceAlreadyExistsException_returnsBadRequest() {
        var response = handler.handleResourceAlreadyExistsException(new ResourceAlreadyExistsException("exists"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("exists");
    }

    @Test
    void handleConstraintViolation_returnsBadRequest() {
        var response = handler.handleConstraintViolation(new ConstraintViolationException("invalid", Set.of()));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Validation error: invalid");
    }

    @Test
    void handleMethodArgumentNotValid_returnsBadRequest() {
        var exception = mock(MethodArgumentNotValidException.class);
        when(exception.getMessage()).thenReturn("invalid body");

        var response = handler.handleMethodArgumentNotValid(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("Validation error: invalid body");
    }

    @Test
    void handleNotFoundException_returnsBadRequest() {
        var response = handler.handleNotFoundException(new NotFoundException("not found"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("not found");
    }

    @Test
    void handleGlobalException_returnsInternalServerError() {
        var response = handler.handleGlobalException(new RuntimeException("failed"), null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOfSatisfying(
                java.util.Map.class,
                body -> assertThat(body).containsEntry("message", "failed"));
    }
}
