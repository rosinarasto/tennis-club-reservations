package com.tennisclub.reservations.security;

import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
public class JwtSecretKeyFactory {

    private static final String HMAC_SHA256 = "HmacSHA256";

    public SecretKeySpec create(String secret) {
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
    }
}
