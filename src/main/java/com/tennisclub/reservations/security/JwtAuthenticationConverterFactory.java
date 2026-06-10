package com.tennisclub.reservations.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class JwtAuthenticationConverterFactory {

    private static final String ROLE_CLAIM = "role";

    public Converter<Jwt, ? extends AbstractAuthenticationToken> create() {
        var jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(this::getAuthorities);

        return jwtAuthenticationConverter;
    }

    private Collection<GrantedAuthority> getAuthorities(Jwt jwt) {
        var role = jwt.getClaimAsString(ROLE_CLAIM);

        if (role == null || role.isBlank()) {
            return List.of();
        }

        return List.of(new SimpleGrantedAuthority(role));
    }
}
