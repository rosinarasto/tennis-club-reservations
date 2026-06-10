package com.tennisclub.reservations.security;

import com.tennisclub.reservations.config.ApiUris;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;

@Component
public class AuthEndpointBearerTokenResolver {

    private final DefaultBearerTokenResolver bearerTokenResolver = new DefaultBearerTokenResolver();

    public String resolve(HttpServletRequest request) {
        if (request.getRequestURI().startsWith(ApiUris.AUTH_URI + "/")) {
            return null;
        }

        return bearerTokenResolver.resolve(request);
    }
}
