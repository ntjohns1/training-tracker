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
 * Comprehensive tests for CORS configuration.
 * Validates cross-origin requests, preflight handling, and allowed origins/methods/headers.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CorsConfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    // ========== Allowed Origins Tests ==========

    @Test
    public void testAllowedOrigin_Localhost3000_ShouldBeAllowed() throws Exception {
        mockMvc.perform(get("/api/mesocycles")
                .header("Origin", "http://localhost:3000"))
                .andExpect(status().isUnauthorized()) // Still unauthorized, but CORS should be handled
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    public void testAllowedOrigin_Localhost8080_ShouldBeAllowed() throws Exception {
        mockMvc.perform(get("/api/mesocycles")
                .header("Origin", "http://localhost:8080"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:8080"));
    }

    @Test
    public void testAllowedOrigin_NelsonJohnsDomain_ShouldBeAllowed() throws Exception {
        mockMvc.perform(get("/api/mesocycles")
                .header("Origin", "https://app.nelsonjohns.com"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("Access-Control-Allow-Origin", "https://app.nelsonjohns.com"));
    }

    @Test
    public void testDisallowedOrigin_ShouldBeRejected() throws Exception {
        mockMvc.perform(get("/api/mesocycles")
                .header("Origin", "https://malicious-site.com"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }

    // ========== Preflight Request Tests ==========

    @Test
    public void testPreflightRequest_GET_ShouldBeAllowed() throws Exception {
        mockMvc.perform(options("/api/mesocycles")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("GET")));
    }

    @Test
    public void testPreflightRequest_POST_ShouldBeAllowed() throws Exception {
        mockMvc.perform(options("/api/mesocycles")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("POST")));
    }

    @Test
    public void testPreflightRequest_PUT_ShouldBeAllowed() throws Exception {
        mockMvc.perform(options("/api/mesocycles")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "PUT"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("PUT")));
    }

    @Test
    public void testPreflightRequest_DELETE_ShouldBeAllowed() throws Exception {
        mockMvc.perform(options("/api/mesocycles")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "DELETE"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("DELETE")));
    }

    @Test
    public void testPreflightRequest_PATCH_ShouldBeAllowed() throws Exception {
        mockMvc.perform(options("/api/mesocycles")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "PATCH"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("PATCH")));
    }

    // ========== Allowed Headers Tests ==========

    @Test
    public void testPreflightRequest_AuthorizationHeader_ShouldBeAllowed() throws Exception {
        mockMvc.perform(options("/api/mesocycles")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET")
                .header("Access-Control-Request-Headers", "Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Headers", org.hamcrest.Matchers.containsString("Authorization")));
    }

    @Test
    public void testPreflightRequest_ContentTypeHeader_ShouldBeAllowed() throws Exception {
        mockMvc.perform(options("/api/mesocycles")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Headers", org.hamcrest.Matchers.containsString("Content-Type")));
    }

    @Test
    public void testPreflightRequest_MultipleHeaders_ShouldBeAllowed() throws Exception {
        mockMvc.perform(options("/api/mesocycles")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST")
                .header("Access-Control-Request-Headers", "Authorization,Content-Type,X-Requested-With"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Headers", org.hamcrest.Matchers.containsString("Authorization")))
                .andExpect(header().string("Access-Control-Allow-Headers", org.hamcrest.Matchers.containsString("Content-Type")))
                .andExpect(header().string("Access-Control-Allow-Headers", org.hamcrest.Matchers.containsString("X-Requested-With")));
    }

    // ========== Credentials Tests ==========

    @Test
    public void testCorsWithCredentials_ShouldBeAllowed() throws Exception {
        mockMvc.perform(get("/api/mesocycles")
                .header("Origin", "http://localhost:3000"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    @Test
    @WithMockUser
    public void testAuthenticatedCorsRequest_ShouldIncludeCredentials() throws Exception {
        mockMvc.perform(get("/api/mesocycles")
                .header("Origin", "http://localhost:3000"))
                .andExpect(status().isNotFound()) // Authenticated, so not 401
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    // ========== Max Age Tests ==========

    @Test
    public void testPreflightMaxAge_ShouldBe3600Seconds() throws Exception {
        mockMvc.perform(options("/api/mesocycles")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Max-Age", "3600"));
    }

    // ========== Complex Preflight Tests ==========

    @Test
    public void testComplexPreflightRequest_ShouldHandleAllAspects() throws Exception {
        mockMvc.perform(options("/api/training/days/123")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "PUT")
                .header("Access-Control-Request-Headers", "Authorization,Content-Type,Accept"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Methods", org.hamcrest.Matchers.containsString("PUT")))
                .andExpect(header().string("Access-Control-Allow-Headers", org.hamcrest.Matchers.containsString("Authorization")))
                .andExpect(header().string("Access-Control-Allow-Headers", org.hamcrest.Matchers.containsString("Content-Type")))
                .andExpect(header().string("Access-Control-Allow-Headers", org.hamcrest.Matchers.containsString("Accept")))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"))
                .andExpect(header().string("Access-Control-Max-Age", "3600"));
    }

    // ========== Actual Request Tests ==========

    @Test
    @WithMockUser
    public void testActualCorsRequest_POST_ShouldWork() throws Exception {
        mockMvc.perform(post("/api/mesocycles")
                .header("Origin", "http://localhost:3000")
                .header("Authorization", "Bearer mock-token")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isNotFound()) // Endpoint doesn't exist, but CORS should work
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    @WithMockUser
    public void testActualCorsRequest_PUT_ShouldWork() throws Exception {
        mockMvc.perform(put("/api/training/sets/123")
                .header("Origin", "http://localhost:3000")
                .header("Authorization", "Bearer mock-token")
                .contentType("application/json")
                .content("{\"weight\": 19}"))
                .andExpect(status().isNotFound()) // Endpoint doesn't exist, but CORS should work
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }
}
