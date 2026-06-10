package com.tennisclub.reservations.auth;

import com.tennisclub.reservations.auth.dto.AuthRequestDto;
import com.tennisclub.reservations.auth.dto.AuthTokenDto;
import com.tennisclub.reservations.auth.dto.RefreshTokenDto;
import com.tennisclub.reservations.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthServiceImpl(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserDetailsService userDetailsService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public AuthTokenDto login(AuthRequestDto request) {
        var authenticationRequest = new UsernamePasswordAuthenticationToken(
                request.getPhoneNumber(),
                request.getPassword()
        );
        var authentication = authenticationManager.authenticate(authenticationRequest);

        return createTokenResponse((UserDetails) authentication.getPrincipal());
    }

    @Override
    public AuthTokenDto refresh(RefreshTokenDto request) {
        var phoneNumber = jwtService.getSubjectFromRefreshToken(request.getRefreshToken());
        var userDetails = userDetailsService.loadUserByUsername(phoneNumber);

        return createTokenResponse(userDetails);
    }

    private AuthTokenDto createTokenResponse(UserDetails userDetails) {
        return new AuthTokenDto(
                jwtService.createAccessToken(userDetails),
                jwtService.createRefreshToken(userDetails)
        );
    }
}
