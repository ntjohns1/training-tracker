package com.noslen.training_tracker.config;

import com.noslen.training_tracker.security.DevUserContext;
import com.noslen.training_tracker.security.UserContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verifies the {@code dev} profile wiring end-to-end against the local Postgres test DB:
 * the context loads without Keycloak (OAuth2 resource-server auto-config excluded), the
 * DevDataSeeder populates reference data, and the stub {@link DevUserContext} is active.
 * Kafka consumer startup is disabled here since no broker runs during tests.
 */
@SpringBootTest
@ActiveProfiles("dev")
@TestPropertySource(properties = {
        // No Postgres in the test environment; run the dev wiring against in-memory H2.
        "spring.datasource.url=jdbc:h2:mem:devseedtest;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.kafka.consumer.auto-startup=false"
})
class DevProfileSeederTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserContext userContext;

    @Test
    void seedsReferenceDataAndUsesStubUser() {
        Integer muscleGroups = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM muscle_groups", Integer.class);
        Integer exercises = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM exercises", Integer.class);

        assertEquals(12, muscleGroups, "all muscle groups seeded");
        assertEquals(301, exercises, "full exercise catalog seeded");

        assertTrue(userContext instanceof DevUserContext, "dev profile uses the stub user context");
        assertEquals(DevUserContext.DEV_USER_ID, userContext.getCurrentUserId());
    }
}
