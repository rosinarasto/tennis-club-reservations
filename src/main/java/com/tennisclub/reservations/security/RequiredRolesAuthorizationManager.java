package com.tennisclub.reservations.security;

import com.tennisclub.reservations.model.Role;
import com.tennisclub.reservations.security.annotation.RequiredRoles;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Supplier;

public class RequiredRolesAuthorizationManager implements AuthorizationManager<MethodInvocation> {

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authentication, MethodInvocation invocation) {
        var requiredRoles = findRequiredRoles(invocation);

        if (requiredRoles == null) {
            return new AuthorizationDecision(true);
        }

        var currentAuthentication = authentication.get();
        var isAuthorized = currentAuthentication != null
                && currentAuthentication.isAuthenticated()
                && hasAnyRequiredRole(currentAuthentication, requiredRoles.value());

        return new AuthorizationDecision(isAuthorized);
    }

    private boolean hasAnyRequiredRole(Authentication authentication, Role[] requiredRoles) {
        return Arrays.stream(requiredRoles)
                .anyMatch(role -> hasAuthority(authentication, role));
    }

    private boolean hasAuthority(Authentication authentication, Role role) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> role.getValue().equals(authority.getAuthority()));
    }

    private RequiredRoles findRequiredRoles(MethodInvocation invocation) {
        var method = invocation.getMethod();
        var target = invocation.getThis();
        var targetClass = target == null ? method.getDeclaringClass() : AopUtils.getTargetClass(target);
        var specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);

        var methodAnnotation = findRequiredRolesOnMethod(specificMethod);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }

        if (!specificMethod.equals(method)) {
            methodAnnotation = findRequiredRolesOnMethod(method);
            if (methodAnnotation != null) {
                return methodAnnotation;
            }
        }

        return AnnotatedElementUtils.findMergedAnnotation(targetClass, RequiredRoles.class);
    }

    private RequiredRoles findRequiredRolesOnMethod(Method method) {
        return AnnotatedElementUtils.findMergedAnnotation(method, RequiredRoles.class);
    }
}
