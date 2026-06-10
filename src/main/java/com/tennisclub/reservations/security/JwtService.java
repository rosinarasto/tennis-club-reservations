package com.tennisclub.reservations.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

/**
 * Creates and validates signed JWT tokens.
 */
@Service
public class JwtService {

    public static final String TOKEN_TYPE_CLAIM = "token_type";
    public static final String ACCESS_TOKEN_TYPE = "access";
    public static final String REFRESH_TOKEN_TYPE = "refresh";

    private static final String ROLE_CLAIM = "role";

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder refreshTokenDecoder;
    private final Duration accessTokenExpiration;
    private final Duration refreshTokenExpiration;

    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${security.jwt.secret}") String secret,
            JwtDecoderFactory jwtDecoderFactory,
            @Value("${security.jwt.access-token-expiration}") Duration accessTokenExpiration,
            @Value("${security.jwt.refresh-token-expiration}") Duration refreshTokenExpiration
    ) {
        this.jwtEncoder = jwtEncoder;
        this.refreshTokenDecoder = jwtDecoderFactory.create(secret, REFRESH_TOKEN_TYPE);
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * Creates an access token for authenticated API requests.
     *
     * @param userDetails authenticated user details.
     * @return signed access token.
     */
    public String createAccessToken(UserDetails userDetails) {
        return createToken(userDetails, ACCESS_TOKEN_TYPE, accessTokenExpiration);
    }

    /**
     * Creates a refresh token for obtaining a new token pair.
     *
     * @param userDetails authenticated user details.
     * @return signed refresh token.
     */
    public String createRefreshToken(UserDetails userDetails) {
        return createToken(userDetails, REFRESH_TOKEN_TYPE, refreshTokenExpiration);
    }

    /**
     * Validates a refresh token and returns its subject.
     *
     * @param token refresh token.
     * @return token subject.
     */
    public String getSubjectFromRefreshToken(String token) {
        var jwt = refreshTokenDecoder.decode(token);

        return jwt.getSubject();
    }

    private String createToken(UserDetails userDetails, String tokenType, Duration tokenExpiration) {
        var now = Instant.now();
        var header = JwsHeader.with(MacAlgorithm.HS256).build();
        var claims = JwtClaimsSet.builder()
                .subject(userDetails.getUsername())
                .claim(ROLE_CLAIM, getRole(userDetails))
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .issuedAt(now)
                .expiresAt(now.plus(tokenExpiration))
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
