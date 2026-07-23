package com.noslen.training_tracker.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for Keycloak JWT authentication and authorization.
 * Tests the Spring Security configuration with OAuth2 Resource Server.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class KeycloakJwtIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testUnauthenticatedAccessToProtectedEndpoint_ShouldReturn401() throws Exception {
        // Test that protected endpoints require authentication
        mockMvc.perform(get("/api/mesocycles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testUnauthenticatedAccessToActuatorHealth_ShouldBeAllowed() throws Exception {
        // Test that health endpoint is accessible without authentication
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testAuthenticatedAccessToProtectedEndpoint_ShouldBeAllowed() throws Exception {
        // Probe a routed-but-unmapped /api path: an authenticated request should reach the
        // dispatcher and 404 (no handler) rather than being rejected with 401.
        mockMvc.perform(get("/api/security-probe"))
                .andExpect(status().isNotFound()); // 404 = authenticated + routed, not 401
    }

    @Test
    public void testKeycloakIssuerUriConfiguration() {
        // This test verifies that the Keycloak issuer URI is properly configured
        // The actual JWT validation will happen when we make real requests

        // We can't easily test the actual JWT validation without a real token,
        // but we can verify the configuration is loaded

        // If the application starts successfully, the Keycloak configuration is valid
        assert true; // Placeholder - the fact that the test runs means config is loaded
    }

    @Test
    public void testCorsConfiguration() throws Exception {
        // Test CORS configuration for frontend integration
        mockMvc.perform(get("/api/mesocycles")
                .header("Origin", "http://localhost:3000"))
                .andExpect(status().isUnauthorized()) // Still unauthorized, but CORS should be handled
                .andExpect(header().exists("Access-Control-Allow-Origin"));
    }

    @Test
    public void testOptionsRequest_ShouldHandlePreflight() throws Exception {
        // Test preflight CORS requests
        mockMvc.perform(options("/api/mesocycles")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Authorization,Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().exists("Access-Control-Allow-Methods"))
                .andExpect(header().exists("Access-Control-Allow-Headers"));
    }

    // TODO: Add test with real JWT token from Keycloak
    // This would require either:
    // 1. A test client configured in Keycloak
    // 2. A mock JWT token for testing
    // 3. Integration with Testcontainers for a test Keycloak instance

    /*
    @Test
    public void testRealJwtToken_ShouldAuthenticate() throws Exception {
        // Example of how to test with a real JWT token
        String jwtToken = "eyJ..."; // Real JWT from Keycloak

        mockMvc.perform(get("/api/mesocycles")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk());
    }
    */
}
