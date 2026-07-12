package com.noslen.training_tracker.security;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

/**
 * Utility class for extracting user information from the current security context.
 * Provides methods to get the current user's ID and other claims from JWT tokens.
 *
 * <p>Active outside the {@code dev} profile; the dev profile uses {@link DevUserContext}.</p>
 */
@Component
@Profile("!dev")
public class UserContext {

    /**
     * Gets the current authenticated user's ID from the JWT token.
     * 
     * @return the user ID as Long, or null if not authenticated or ID not found
     * @throws IllegalStateException if user is not authenticated
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated");
        }
        
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            // Try different common JWT claims for user ID
            Object userIdClaim = jwt.getClaim("sub"); // Standard subject claim
            if (userIdClaim == null) {
                userIdClaim = jwt.getClaim("user_id"); // Custom user_id claim
            }
            if (userIdClaim == null) {
                userIdClaim = jwt.getClaim("uid"); // Alternative uid claim
            }
            
            if (userIdClaim != null) {
                if (userIdClaim instanceof String) {
                    try {
                        return Long.parseLong((String) userIdClaim);
                    } catch (NumberFormatException e) {
                        throw new IllegalStateException("User ID claim is not a valid number: " + userIdClaim);
                    }
                } else if (userIdClaim instanceof Number) {
                    return ((Number) userIdClaim).longValue();
                }
            }
        }
        
        throw new IllegalStateException("Unable to extract user ID from authentication token");
    }
    
    /**
     * Gets the current authenticated user's username/email from the JWT token.
     * 
     * @return the username/email as String, or null if not found
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        
        if (authentication.getPrincipal() instanceof Jwt jwt) {
            // Try different common JWT claims for username
            String username = jwt.getClaim("preferred_username");
            if (username == null) {
                username = jwt.getClaim("email");
            }
            if (username == null) {
                username = jwt.getClaim("username");
            }
            return username;
        }
        
        return authentication.getName();
    }
    
    /**
     * Checks if the current user has access to resources belonging to the specified user ID.
     * 
     * @param resourceUserId the user ID that owns the resource
     * @return true if the current user can access the resource, false otherwise
     */
    public boolean canAccessUserResource(Long resourceUserId) {
        if (resourceUserId == null) {
            return false;
        }
        
        try {
            Long currentUserId = getCurrentUserId();
            return currentUserId != null && currentUserId.equals(resourceUserId);
        } catch (IllegalStateException e) {
            return false;
        }
    }
    
    /**
     * Validates that the current user has access to resources belonging to the specified user ID.
     * Throws an exception if access is denied.
     * 
     * @param resourceUserId the user ID that owns the resource
     * @throws SecurityException if the current user cannot access the resource
     */
    public void validateUserAccess(Long resourceUserId) {
        if (!canAccessUserResource(resourceUserId)) {
            throw new SecurityException("Access denied: User does not have permission to access this resource");
        }
    }
}
