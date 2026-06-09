package com.tennisclub.reservations.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

/**
 * Creates signed JWT access tokens.
 */
@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final Duration accessTokenExpiration;

    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${security.jwt.access-token-expiration}") Duration accessTokenExpiration
    ) {
        this.jwtEncoder = jwtEncoder;
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public String createAccessToken(UserDetails userDetails) {
        var now = Instant.now();
        var header = JwsHeader.with(MacAlgorithm.HS256).build();
        var claims = JwtClaimsSet.builder()
                .subject(userDetails.getUsername())
                .claim("role", getRole(userDetails))
                .issuedAt(now)
                .expiresAt(now.plus(accessTokenExpiration))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }

    private String getRole(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");
    }
}
