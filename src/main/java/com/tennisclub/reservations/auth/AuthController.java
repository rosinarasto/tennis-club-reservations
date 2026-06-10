package com.tennisclub.reservations.auth;

import com.tennisclub.reservations.auth.dto.AuthRequestDto;
import com.tennisclub.reservations.auth.dto.AuthTokenDto;
import com.tennisclub.reservations.auth.dto.RefreshTokenDto;
import com.tennisclub.reservations.config.ApiUris;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles authentication endpoints.
 */
@RestController
@RequestMapping(ApiUris.AUTH_URI)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(ApiUris.LOGIN_URI)
    public ResponseEntity<AuthTokenDto> login(@Valid @RequestBody AuthRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping(ApiUris.REFRESH_URI)
    public ResponseEntity<AuthTokenDto> refresh(@Valid @RequestBody RefreshTokenDto request) {
        return ResponseEntity.ok(authService.refresh(request));
    }
}
