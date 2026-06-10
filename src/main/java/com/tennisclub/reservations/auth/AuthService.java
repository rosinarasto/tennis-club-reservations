package com.tennisclub.reservations.auth;

import com.tennisclub.reservations.auth.dto.AuthRequestDto;
import com.tennisclub.reservations.auth.dto.AuthTokenDto;
import com.tennisclub.reservations.auth.dto.RefreshTokenDto;

/**
 * Handles authentication token issuing.
 */
public interface AuthService {

    /**
     * Authenticates a user and creates access and refresh tokens.
     */
    AuthTokenDto login(AuthRequestDto request);

    /**
     * Validates a refresh token and creates a new token pair.
     */
    AuthTokenDto refresh(RefreshTokenDto request);
}
