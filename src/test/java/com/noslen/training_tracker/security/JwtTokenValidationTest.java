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
 * Tests for JWT token validation and OAuth2 Resource Server behavior.
 * Validates token format, authorization header handling, and authentication flow.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class JwtTokenValidationTest {

    @Autowired
    private MockMvc mockMvc;

    // ========== Authorization Header Format Tests ==========

    @Test
    public void testMissingAuthorizationHeader_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/mesocycles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testEmptyAuthorizationHeader_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/mesocycles")
                .header("Authorization", ""))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testInvalidAuthorizationHeaderFormat_ShouldReturn401() throws Exception {
        // Missing "Bearer " prefix
        mockMvc.perform(get("/api/mesocycles")
                .header("Authorization", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testInvalidBearerTokenFormat_ShouldReturn401() throws Exception {
        // Invalid JWT format (not three parts separated by dots)
        mockMvc.perform(get("/api/mesocycles")
                .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testMalformedJwtToken_ShouldReturn401() throws Exception {
        // Malformed JWT (only two parts)
        mockMvc.perform(get("/api/mesocycles")
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testExpiredJwtToken_ShouldReturn401() throws Exception {
        // This would be an expired JWT token in real scenario
        // For now, any invalid token will return 401
        mockMvc.perform(get("/api/mesocycles")
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwiZXhwIjoxNTE2MjM5MDIyfQ.invalid-signature"))
                .andExpect(status().isUnauthorized());
    }

    // ========== Case Sensitivity Tests ==========

    @Test
    public void testBearerCaseSensitivity_LowerCase_ShouldReturn401() throws Exception {
        // "bearer" instead of "Bearer"
        mockMvc.perform(get("/api/mesocycles")
                .header("Authorization", "bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.signature"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testBearerCaseSensitivity_UpperCase_ShouldReturn401() throws Exception {
        // "BEARER" instead of "Bearer"
        mockMvc.perform(get("/api/mesocycles")
                .header("Authorization", "BEARER eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIn0.signature"))
                .andExpect(status().isUnauthorized());
    }

    // ========== Multiple Authorization Headers Tests ==========

    @Test
    public void testMultipleAuthorizationHeaders_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/mesocycles")
                .header("Authorization", "Bearer token1")
                .header("Authorization", "Bearer token2"))
                .andExpect(status().isUnauthorized());
    }

    // ========== Token Length Tests ==========

    @Test
    public void testVeryShortToken_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/mesocycles")
                .header("Authorization", "Bearer a.b.c"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testVeryLongToken_ShouldReturn401() throws Exception {
        // Extremely long but invalid token
        String longToken = "Bearer " + "a".repeat(1000) + "." + "b".repeat(1000) + "." + "c".repeat(1000);
        mockMvc.perform(get("/api/mesocycles")
                .header("Authorization", longToken))
                .andExpect(status().isUnauthorized());
    }

    // ========== Special Characters in Token Tests ==========

    @Test
    public void testTokenWithSpecialCharacters_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/mesocycles")
                .header("Authorization", "Bearer token@#$%.token@#$%.token@#$%"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testTokenWithSpaces_ShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/mesocycles")
                .header("Authorization", "Bearer token with spaces.token with spaces.token with spaces"))
                .andExpect(status().isUnauthorized());
    }

    // ========== Mock User Tests (Simulating Valid JWT) ==========

    @Test
    @WithMockUser(username = "user123", roles = {"USER"})
    public void testValidAuthentication_ShouldAllowAccess() throws Exception {
        // With valid authentication, should get 404 (not found) not 401 (unauthorized)
        mockMvc.perform(get("/api/mesocycles"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testAdminAuthentication_ShouldAllowAccess() throws Exception {
        mockMvc.perform(get("/api/mesocycles"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"SCOPE_read", "SCOPE_write"})
    public void testScopeBasedAuthentication_ShouldAllowAccess() throws Exception {
        // Test with OAuth2 scopes
        mockMvc.perform(get("/api/mesocycles"))
                .andExpect(status().isNotFound());
    }

    // ========== Different Endpoint Tests ==========

    @Test
    public void testJwtValidationOnDifferentEndpoints() throws Exception {
        // Test that JWT validation applies to all API endpoints
        String[] endpoints = {
            "/api/mesocycles",
            "/api/days",
            "/api/exercises",
            "/api/training/days/123",
            "/api/training/sets/456"
        };

        for (String endpoint : endpoints) {
            mockMvc.perform(get(endpoint))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    @WithMockUser
    public void testAuthenticatedAccessToDifferentEndpoints() throws Exception {
        // Test that authenticated access works for all API endpoints
        String[] endpoints = {
            "/api/mesocycles",
            "/api/days", 
            "/api/exercises",
            "/api/training/days/123",
            "/api/training/sets/456"
        };

        for (String endpoint : endpoints) {
            mockMvc.perform(get(endpoint))
                    .andExpect(status().isNotFound()); // Not 401 Unauthorized
        }
    }

    // ========== HTTP Methods with JWT Tests ==========

    @Test
    @WithMockUser
    public void testAllHttpMethodsWithValidAuth_ShouldWork() throws Exception {
        String endpoint = "/api/mesocycles";

        mockMvc.perform(get(endpoint)).andExpect(status().isNotFound());
        mockMvc.perform(post(endpoint).contentType("application/json").content("{}")).andExpect(status().isNotFound());
        mockMvc.perform(put(endpoint + "/1").contentType("application/json").content("{}")).andExpect(status().isNotFound());
        mockMvc.perform(patch(endpoint + "/1").contentType("application/json").content("{}")).andExpect(status().isNotFound());
        mockMvc.perform(delete(endpoint + "/1")).andExpect(status().isNotFound());
    }
}
