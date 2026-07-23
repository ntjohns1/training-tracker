package com.noslen.training_tracker.security;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Aspect that intercepts methods annotated with @RequireUserAccess
 * and automatically validates user access to resources.
 */
@Aspect
@Component
public class UserAccessAspect {

    private final UserContext userContext;

    public UserAccessAspect(UserContext userContext) {
        this.userContext = userContext;
    }

    /**
     * Intercepts methods annotated with @RequireUserAccess and validates user access.
     */
    @Before("@annotation(requireUserAccess)")
    public void validateUserAccess(JoinPoint joinPoint, RequireUserAccess requireUserAccess) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();

        // Find the parameter that contains the user ID
        String userIdParamName = requireUserAccess.userIdParam();
        Long userIdToValidate = null;

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(userIdParamName)) {
                Object paramValue = args[i];
                if (paramValue instanceof Long) {
                    userIdToValidate = (Long) paramValue;
                    break;
                } else if (paramValue != null) {
                    throw new IllegalArgumentException(
                        "Parameter '" + userIdParamName + "' must be of type Long, but was: " + paramValue.getClass()
                    );
                }
            }
        }

        if (userIdToValidate == null) {
            throw new IllegalArgumentException(
                "Could not find parameter '" + userIdParamName + "' in method " + method.getName()
            );
        }

        // Validate user access
        try {
            userContext.validateUserAccess(userIdToValidate);
        } catch (SecurityException e) {
            throw new SecurityException(requireUserAccess.message(), e);
        }
    }
}
