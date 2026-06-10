package com.tennisclub.reservations.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.tennisclub.reservations.security.JwtDecoderFactory;
import com.tennisclub.reservations.security.JwtSecretKeyFactory;
import com.tennisclub.reservations.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
public class JwtTokenConfig {

    @Bean
    public JwtEncoder jwtEncoder(
            @Value("${security.jwt.secret}") String secret,
            JwtSecretKeyFactory jwtSecretKeyFactory
    ) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKeyFactory.create(secret)));
    }

    @Bean
    public JwtDecoder jwtDecoder(
            @Value("${security.jwt.secret}") String secret,
            JwtDecoderFactory jwtDecoderFactory
    ) {
        return jwtDecoderFactory.create(secret, JwtService.ACCESS_TOKEN_TYPE);
    }
}
