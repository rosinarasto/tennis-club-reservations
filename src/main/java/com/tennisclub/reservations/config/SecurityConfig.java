package com.tennisclub.reservations.config;

import com.tennisclub.reservations.security.AuthEndpointBearerTokenResolver;
import com.tennisclub.reservations.security.JwtAuthenticationConverterFactory;
import com.tennisclub.reservations.security.RequiredRolesAuthorizationManager;
import com.tennisclub.reservations.security.RestAccessDeniedHandler;
import com.tennisclub.reservations.security.RestAuthenticationEntryPoint;
import com.tennisclub.reservations.security.annotation.RequiredRoles;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.method.AuthorizationManagerBeforeMethodInterceptor;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthEndpointBearerTokenResolver bearerTokenResolver;
    private final JwtAuthenticationConverterFactory jwtAuthenticationConverterFactory;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    public SecurityConfig(
            AuthEndpointBearerTokenResolver bearerTokenResolver,
            JwtAuthenticationConverterFactory jwtAuthenticationConverterFactory,
            RestAuthenticationEntryPoint restAuthenticationEntryPoint,
            RestAccessDeniedHandler restAccessDeniedHandler
    ) {
        this.bearerTokenResolver = bearerTokenResolver;
        this.jwtAuthenticationConverterFactory = jwtAuthenticationConverterFactory;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(requests -> requests
                    .requestMatchers(
                            ApiUris.AUTH_URI + "/**",
                            "/v3/api-docs/**",
                            "/swagger-ui.html",
                            "/swagger-ui/**"
                    ).permitAll()
                    .anyRequest().authenticated())
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions
                    .authenticationEntryPoint(restAuthenticationEntryPoint)
                    .accessDeniedHandler(restAccessDeniedHandler))
            .oauth2ResourceServer(resourceServer -> resourceServer
                    .authenticationEntryPoint(restAuthenticationEntryPoint)
                    .bearerTokenResolver(bearerTokenResolver::resolve)
                    .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverterFactory.create())));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public static AuthorizationManagerBeforeMethodInterceptor requiredRolesAuthorizationManager() {
        var methodPointcut = AnnotationMatchingPointcut.forMethodAnnotation(RequiredRoles.class);
        var classPointcut = AnnotationMatchingPointcut.forClassAnnotation(RequiredRoles.class);
        var requiredRolesPointcut = new ComposablePointcut(methodPointcut).union(classPointcut);

        return new AuthorizationManagerBeforeMethodInterceptor(
                requiredRolesPointcut,
                new RequiredRolesAuthorizationManager()
        );
    }
}
