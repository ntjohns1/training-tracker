package com.noslen.training_tracker.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive tests for OAuth2 Resource Server configuration.
 * Validates JWT authentication, authorization rules, and endpoint security.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OAuth2ResourceServerConfigTest {

    @Autowired
    private MockMvc mockMvc;

    // ========== Public Endpoint Tests ==========

    @Test
    public void testActuatorHealthEndpoint_ShouldBePublic() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    public void testActuatorInfoEndpoint_ShouldBePublic() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/vnd.spring-boot.actuator.v3+json"));
    }

    @Test
    public void testErrorEndpoint_ShouldBePublic() throws Exception {
        mockMvc.perform(get("/error"))
                .andExpect(status().is5xxServerError()); // Error endpoint returns 500 by default
    }

    // ========== Protected API Endpoint Tests ==========

    @Test
    public void testApiEndpoints_ShouldRequireAuthentication() throws Exception {
        // Test various API endpoints require authentication
        mockMvc.perform(get("/api/mesocycles"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/days"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/exercises"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/mesocycles"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/api/mesocycles/1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/api/mesocycles/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testNestedApiEndpoints_ShouldRequireAuthentication() throws Exception {
        // Test nested API endpoints
        mockMvc.perform(get("/api/training/days/123"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/api/training/sets/456"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/training/mesocycles"))
                .andExpect(status().isUnauthorized());
    }

    // ========== Authenticated Access Tests ==========

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testAuthenticatedApiAccess_ShouldAllowAccess() throws Exception {
        // With authentication, should get 404 (endpoint doesn't exist) not 401 (unauthorized)
        mockMvc.perform(get("/api/mesocycles"))
                .andExpect(status().isNotFound()); // Not 401 Unauthorized

        mockMvc.perform(get("/api/training/days/123"))
                .andExpect(status().isNotFound()); // Not 401 Unauthorized
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN", "USER"})
    public void testAdminUserAccess_ShouldAllowAccess() throws Exception {
        // Admin users should also have access
        mockMvc.perform(get("/api/mesocycles"))
                .andExpect(status().isNotFound()); // Not 401 Unauthorized

        mockMvc.perform(post("/api/mesocycles"))
                .andExpect(status().isNotFound()); // Not 401 Unauthorized
    }

    // ========== HTTP Method Tests ==========

    @Test
    public void testAllHttpMethods_ShouldRequireAuthentication() throws Exception {
        String endpoint = "/api/mesocycles";

        mockMvc.perform(get(endpoint)).andExpect(status().isUnauthorized());
        mockMvc.perform(post(endpoint)).andExpect(status().isUnauthorized());
        mockMvc.perform(put(endpoint + "/1")).andExpect(status().isUnauthorized());
        mockMvc.perform(patch(endpoint + "/1")).andExpect(status().isUnauthorized());
        mockMvc.perform(delete(endpoint + "/1")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void testAllHttpMethodsAuthenticated_ShouldAllowAccess() throws Exception {
        String endpoint = "/api/mesocycles";

        // All should return 404 (not found) rather than 401 (unauthorized)
        mockMvc.perform(get(endpoint)).andExpect(status().isNotFound());
        mockMvc.perform(post(endpoint)).andExpect(status().isNotFound());
        mockMvc.perform(put(endpoint + "/1")).andExpect(status().isNotFound());
        mockMvc.perform(patch(endpoint + "/1")).andExpect(status().isNotFound());
        mockMvc.perform(delete(endpoint + "/1")).andExpect(status().isNotFound());
    }

    // ========== Session Management Tests ==========

    @Test
    public void testStatelessSessionManagement_ShouldNotCreateSession() throws Exception {
        // Verify that no session is created (stateless JWT authentication)
        mockMvc.perform(get("/api/mesocycles"))
                .andExpect(status().isUnauthorized())
                .andExpect(request().sessionAttribute("SPRING_SECURITY_CONTEXT", org.hamcrest.Matchers.nullValue()));
    }

    // ========== Authorization Header Tests ==========

    @Test
    public void testMissingAuthorizationHeader_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/mesocycles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testInvalidAuthorizationHeader_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/mesocycles")
                .header("Authorization", "Invalid token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testMalformedBearerToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/mesocycles")
                .header("Authorization", "Bearer invalid.jwt.token"))
                .andExpect(status().isUnauthorized());
    }

    // ========== Content Type Tests ==========

    @Test
    @WithMockUser
    public void testJsonContentType_ShouldBeAccepted() throws Exception {
        mockMvc.perform(post("/api/mesocycles")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isNotFound()); // Not 415 Unsupported Media Type
    }

    @Test
    @WithMockUser
    public void testXmlContentType_ShouldBeAccepted() throws Exception {
        mockMvc.perform(post("/api/mesocycles")
                .contentType("application/xml")
                .content("<test/>"))
                .andExpect(status().isNotFound()); // Not 415 Unsupported Media Type
    }
}
