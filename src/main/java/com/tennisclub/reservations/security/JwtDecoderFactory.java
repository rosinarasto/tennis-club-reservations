package com.tennisclub.reservations.security;

import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

/**
 * Creates JWT decoders with token-type validation.
 */
@Component
public class JwtDecoderFactory {

    private final JwtSecretKeyFactory jwtSecretKeyFactory;

    public JwtDecoderFactory(JwtSecretKeyFactory jwtSecretKeyFactory) {
        this.jwtSecretKeyFactory = jwtSecretKeyFactory;
    }

    /**
     * Creates a decoder that validates token signature, expiration and expected token type.
     *
     * @param secret shared signing secret.
     * @param expectedTokenType expected value of the token-type claim.
     * @return configured JWT decoder.
     */
    public JwtDecoder create(String secret, String expectedTokenType) {
        var decoder = NimbusJwtDecoder.withSecretKey(jwtSecretKeyFactory.create(secret))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(),
                jwt -> validateTokenType(jwt, expectedTokenType)
        ));

        return decoder;
    }

    private OAuth2TokenValidatorResult validateTokenType(Jwt jwt, String expectedTokenType) {
        if (expectedTokenType.equals(jwt.getClaimAsString(JwtService.TOKEN_TYPE_CLAIM))) {
            return OAuth2TokenValidatorResult.success();
        }

        var error = new OAuth2Error("invalid_token", "Invalid token type", null);
        return OAuth2TokenValidatorResult.failure(error);
    }
}
