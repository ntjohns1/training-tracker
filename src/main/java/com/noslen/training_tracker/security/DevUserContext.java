package com.noslen.training_tracker.security;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Dev-profile stand-in for {@link UserContext}: there is no JWT in local dev (Keycloak is not
 * running and the resource-server auto-config is excluded), so this returns a fixed stub user and
 * permits all access. Injected by type wherever {@code UserContext} is required.
 */
@Component
@Profile("dev")
public class DevUserContext extends UserContext {

    /** Fixed local dev user id. Seed data / created mesocycles are owned by this user. */
    public static final Long DEV_USER_ID = 1L;

    @Override
    public Long getCurrentUserId() {
        return DEV_USER_ID;
    }

    @Override
    public String getCurrentUsername() {
        return "dev@local";
    }

    @Override
    public boolean canAccessUserResource(Long resourceUserId) {
        return true;
    }
}
