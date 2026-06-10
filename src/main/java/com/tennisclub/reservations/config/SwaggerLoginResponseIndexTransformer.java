package com.tennisclub.reservations.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Adds Swagger UI behavior that automatically authorizes the access token returned by the login endpoint.
 */
public class SwaggerLoginResponseIndexTransformer extends SwaggerIndexPageTransformer {

    private static final String SWAGGER_INITIALIZER = "swagger-initializer.js";
    private static final String PRESETS_CONFIG = "presets: [";
    private static final String LOGIN_RESPONSE_INTERCEPTOR = """
            responseInterceptor: (response) => {
              const url = new URL(response.url, window.location.origin);
              const isLoginResponse = url.pathname === '/api/auth/login' && response.status >= 200 && response.status < 300;
              const accessToken = response.obj && response.obj.accessToken;

              if (isLoginResponse && accessToken && window.ui && window.ui.authActions) {
                window.ui.authActions.authorize({
                  bearerAuth: {
                    name: 'bearerAuth',
                    schema: {
                      type: 'http',
                      in: 'header',
                      scheme: 'bearer',
                      bearerFormat: 'JWT'
                    },
                    value: accessToken
                  }
                });
              }

              return response;
            },
            presets: [""";

    public SwaggerLoginResponseIndexTransformer(
            SwaggerUiConfigProperties swaggerUiConfig,
            SwaggerUiOAuthProperties swaggerUiOAuthProperties,
            SwaggerWelcomeCommon swaggerWelcomeCommon,
            ObjectMapperProvider objectMapperProvider
    ) {
        super(swaggerUiConfig, swaggerUiOAuthProperties, swaggerWelcomeCommon, objectMapperProvider);
    }

    @Override
    public Resource transform(
            HttpServletRequest request,
            Resource resource,
            ResourceTransformerChain transformerChain
    ) throws IOException {
        var transformedResource = super.transform(request, resource, transformerChain);

        if (!SWAGGER_INITIALIZER.equals(transformedResource.getFilename())) {
            return transformedResource;
        }

        var swaggerInitializer = new String(transformedResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8)
                .replace(PRESETS_CONFIG, LOGIN_RESPONSE_INTERCEPTOR);

        return new TransformedResource(transformedResource, swaggerInitializer.getBytes(StandardCharsets.UTF_8));
    }
}
