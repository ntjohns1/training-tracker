package com.noslen.training_tracker.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark service methods that require user access validation.
 * When applied to a method, it will automatically validate that the current user
 * has access to resources belonging to the specified user ID parameter.
 * 
 * Usage:
 * @RequireUserAccess(userIdParam = "userId")
 * public SomeResponse getSomeUserResource(Long userId, ...) { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireUserAccess {
    
    /**
     * The name of the method parameter that contains the user ID to validate against.
     * Default is "userId".
     */
    String userIdParam() default "userId";
    
    /**
     * Optional message to include in the SecurityException if access is denied.
     */
    String message() default "Access denied: User does not have permission to access this resource";
}
