package com.tennisclub.reservations.config;

import com.tennisclub.reservations.exception.NotFoundException;
import com.tennisclub.reservations.exception.ResourceAlreadyExistsException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleNullPointerException_returnsBadRequest() {
        var response = handler.handleNullPointerException(new NullPointerException("missing"), request());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("missing");
        assertThat(response.getBody().path()).isEqualTo("/api/test");
    }

    @Test
    void handleResourceAlreadyExistsException_returnsBadRequest() {
        var response = handler.handleResourceAlreadyExistsException(new ResourceAlreadyExistsException("exists"), request());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("exists");
    }

    @Test
    void handleConstraintViolation_returnsBadRequest() {
        var response = handler.handleConstraintViolation(new ConstraintViolationException("invalid", Set.of()), request());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Validation failed");
    }

    @Test
    void handleMethodArgumentNotValid_returnsBadRequest() {
        var exception = mock(MethodArgumentNotValidException.class, RETURNS_DEEP_STUBS);

        var response = handler.handleMethodArgumentNotValid(exception, request());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Validation failed");
    }

    @Test
    void handleNotFoundException_returnsBadRequest() {
        var response = handler.handleNotFoundException(new NotFoundException("not found"), request());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("not found");
    }

    @Test
    void handleBadCredentialsException_returnsUnauthorized() {
        var response = handler.handleBadCredentialsException(new BadCredentialsException("bad credentials"), request());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Invalid credentials");
    }

    @Test
    void handleAuthorizationDeniedException_returnsForbidden() {
        var response = handler.handleAuthorizationDeniedException(new AuthorizationDeniedException("access denied"), request());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Access denied");
    }

    @Test
    void handleJwtException_returnsUnauthorized() {
        var response = handler.handleJwtException(new JwtException("invalid token"), request());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("Invalid token");
    }

    @Test
    void handleGlobalException_returnsInternalServerError() {
        var response = handler.handleGlobalException(new RuntimeException("failed"), request());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().message()).isEqualTo("failed");
    }

    private jakarta.servlet.http.HttpServletRequest request() {
        var request = mock(jakarta.servlet.http.HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/test");
        return request;
    }
}
