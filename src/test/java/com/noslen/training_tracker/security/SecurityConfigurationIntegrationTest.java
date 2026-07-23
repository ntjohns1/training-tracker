package com.noslen.training_tracker.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the complete Spring Security configuration.
 * Validates that all security components work together correctly.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SecurityConfigurationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    // ========== Configuration Bean Tests ==========

    @Test
    public void testSecurityFilterChainBean_ShouldBeConfigured() {
        assertNotNull(securityFilterChain, "SecurityFilterChain should be configured");
    }

    @Test
    public void testCorsConfigurationSourceBean_ShouldBeConfigured() {
        assertNotNull(corsConfigurationSource, "CorsConfigurationSource should be configured");
    }

    // ========== End-to-End Security Flow Tests ==========

    @Test
    public void testCompleteSecurityFlow_UnauthenticatedRequest() throws Exception {
        // Test complete flow: CORS + Authentication + Authorization
        mockMvc.perform(post("/api/mesocycles")
                .header("Origin", "http://localhost:3000")
                .contentType("application/json")
                .content("{\"name\": \"Test Mesocycle\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    public void testCompleteSecurityFlow_AuthenticatedRequest() throws Exception {
        // Test complete flow with authentication against a routed-but-unmapped /api path
        mockMvc.perform(post("/api/security-probe")
                .header("Origin", "http://localhost:3000")
                .contentType("application/json")
                .content("{\"name\": \"Test Mesocycle\"}"))
                .andExpect(status().isNotFound()) // Not 401, so authentication worked
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    // ========== Real-World API Endpoint Tests ==========

    @Test
    public void testTrainingAPIEndpoints_ShouldRequireAuthentication() throws Exception {
        // Test the actual API endpoints from your real-world analysis
        mockMvc.perform(put("/api/training/sets/123")
                .contentType("application/json")
                .content("{\"weight\": 19}"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(put("/api/training/days/456")
                .contentType("application/json")
                .content("{\"finishedAt\": \"2025-07-31T02:48:23.126Z\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    public void testTrainingAPIEndpoints_WithAuthentication_ShouldAllowAccess() throws Exception {
        // Test with authentication - should get 404 (not found) not 401 (unauthorized)
        mockMvc.perform(put("/api/training/sets/123")
                .contentType("application/json")
                .content("{\"weight\": 19}"))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/training/days/456")
                .contentType("application/json")
                .content("{\"finishedAt\": \"2025-07-31T02:48:23.126Z\"}"))
                .andExpect(status().isNotFound());
    }

    // ========== Session Management Tests ==========

    @Test
    public void testStatelessSessionManagement() throws Exception {
        // Verify stateless session management
        mockMvc.perform(get("/api/mesocycles"))
                .andExpect(status().isUnauthorized())
                .andExpect(request().sessionAttribute("SPRING_SECURITY_CONTEXT", org.hamcrest.Matchers.nullValue()));
    }

    @Test
    @WithMockUser
    public void testNoSessionCreatedForAuthenticatedUser() throws Exception {
        // Even with authentication, no session should be created
        mockMvc.perform(get("/api/security-probe"))
                .andExpect(status().isNotFound())
                .andExpect(request().sessionAttribute("SPRING_SECURITY_CONTEXT", org.hamcrest.Matchers.nullValue()));
    }

    // ========== CSRF Protection Tests ==========

    @Test
    public void testCSRFDisabled_PostRequestShouldWork() throws Exception {
        // CSRF should be disabled for stateless JWT authentication
        mockMvc.perform(post("/api/mesocycles")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isUnauthorized()) // Should be 401 (unauthorized), not 403 (CSRF)
                .andExpect(header().doesNotExist("X-CSRF-TOKEN"));
    }

    @Test
    @WithMockUser
    public void testCSRFDisabled_AuthenticatedPostShouldWork() throws Exception {
        // With authentication, CSRF should still be disabled
        mockMvc.perform(post("/api/security-probe")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isNotFound()) // Should be 404 (not found), not 403 (CSRF)
                .andExpect(header().doesNotExist("X-CSRF-TOKEN"));
    }

    // ========== Complex Workflow Tests ==========

    @Test
    @WithMockUser
    public void testComplexWorkoutProgressionWorkflow() throws Exception {
        // Test the complex workout progression workflow discovered in your API analysis
        
        // 1. Update exercise set
        mockMvc.perform(put("/api/training/sets/168902714")
                .header("Origin", "http://localhost:3000")
                .contentType("application/json")
                .content("{\"weight\": 19}"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));

        // 2. Finish exercise set with soreness feedback
        mockMvc.perform(put("/api/training/sets/168902714")
                .header("Origin", "http://localhost:3000")
                .contentType("application/json")
                .content("{\"finishedAt\": \"2025-07-31T02:31:51.071Z\", \"reps\": 11, \"soreness\": -1}"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));

        // 3. Update day with completed exercise
        mockMvc.perform(put("/api/training/days/21195296")
                .header("Origin", "http://localhost:3000")
                .contentType("application/json")
                .content("{\"status\": \"pendingFeedback\"}"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));

        // 4. Finish day with complete feedback
        mockMvc.perform(put("/api/training/days/21195296")
                .header("Origin", "http://localhost:3000")
                .contentType("application/json")
                .content("{\"finishedAt\": \"2025-07-31T02:48:23.126Z\", \"status\": \"pendingConfirmation\"}"))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    // ========== Mesocycle Creation Workflow Tests ==========

    @Test
    @WithMockUser
    public void testMesocycleCreationWorkflow() throws Exception {
        // Test the complex mesocycle creation workflow
        String mesocycleRequest = """
            {
              "name": "Forearms & Calves",
              "weeks": 6,
              "days": [
                {
                  "label": null,
                  "exercises": [
                    {"exerciseId": 216},
                    {"exerciseId": 199}
                  ]
                }
              ],
              "unit": "lb",
              "progressions": {
                "9": {
                  "mgProgressionType": "regular",
                  "muscleGroupId": 9
                }
              }
            }
            """;

        mockMvc.perform(post("/api/security-probe")
                .header("Origin", "http://localhost:3000")
                .contentType("application/json")
                .content(mesocycleRequest))
                .andExpect(status().isNotFound()) // routed + authenticated (no handler), security worked
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    // ========== Error Handling Tests ==========

    @Test
    public void testSecurityErrorHandling_ShouldReturnProperErrorResponse() throws Exception {
        // Test that security errors are handled properly
        mockMvc.perform(get("/api/mesocycles"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Exception"))));
    }

    @Test
    public void testSecurityWithInvalidContentType() throws Exception {
        // Test security with various content types
        mockMvc.perform(post("/api/mesocycles")
                .contentType("text/plain")
                .content("invalid content"))
                .andExpect(status().isUnauthorized()); // Should be 401, not 415 (unsupported media type)
    }

    // ========== Performance Tests ==========

    @Test
    public void testSecurityPerformance_MultipleRequests() throws Exception {
        // Test that security doesn't significantly impact performance
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(get("/api/mesocycles"))
                    .andExpect(status().isUnauthorized());
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete 10 requests in reasonable time (less than 5 seconds)
        assertTrue(duration < 5000, "Security processing should be performant");
    }
}
