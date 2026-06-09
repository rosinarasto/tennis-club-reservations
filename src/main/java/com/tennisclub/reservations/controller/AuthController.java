package com.tennisclub.reservations.controller;

import com.tennisclub.reservations.config.ApiUris;
import com.tennisclub.reservations.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationConverter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles authentication endpoints.
 */
@RestController
@RequestMapping(ApiUris.AUTH_URI)
public class AuthController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final BasicAuthenticationConverter basicAuthenticationConverter;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            BasicAuthenticationConverter basicAuthenticationConverter
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.basicAuthenticationConverter = basicAuthenticationConverter;
    }

    @PostMapping(ApiUris.LOGIN_URI)
    public ResponseEntity<Void> login(HttpServletRequest request) {
        var authenticationRequest = getAuthenticationRequest(request);
        var authentication = authenticationManager.authenticate(authenticationRequest);
        var userDetails = (UserDetails) authentication.getPrincipal();
        var token = jwtService.createAccessToken(userDetails);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + token)
                .build();
    }

    private UsernamePasswordAuthenticationToken getAuthenticationRequest(HttpServletRequest request) {
        var authentication = basicAuthenticationConverter.convert(request);

        if (authentication == null) {
            throw new BadCredentialsException("Missing basic authentication header");
        }

        return authentication;
    }
}
