package com.tennisclub.reservations.security;

import com.tennisclub.reservations.config.ApiUris;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;

/**
 * Resolves Bearer tokens while intentionally ignoring Authorization headers on authentication endpoints.
 */
@Component
public class AuthEndpointBearerTokenResolver {

    private final DefaultBearerTokenResolver bearerTokenResolver = new DefaultBearerTokenResolver();

    /**
     * Resolves the request Bearer token unless the request targets an authentication endpoint.
     *
     * @param request current HTTP request.
     * @return resolved token or {@code null} when no token should be used.
     */
    public String resolve(HttpServletRequest request) {
        if (request.getRequestURI().startsWith(ApiUris.AUTH_URI + "/")) {
            return null;
        }

        return bearerTokenResolver.resolve(request);
    }
}
