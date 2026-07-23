package com.noslen.training_tracker.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noslen.training_tracker.enums.MgName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Seeds reference data (muscle groups + the exercise catalog) for the {@code dev} profile so the
 * app is usable without a manual database import. Idempotent: skips tables that already hold data.
 *
 * <p>Inserts use explicit ids via {@link JdbcTemplate} so catalog ids stay stable (mesocycles
 * reference {@code muscle_group_id}/{@code exercise_id}). The identity sequences are not advanced;
 * that is acceptable for read-mostly reference data in local dev.</p>
 */
@Component
@Profile("dev")
public class DevDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);
    private static final String EXERCISE_SEED = "seed/exercises.json";

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public DevDataSeeder(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        seedMuscleGroups();
        seedExercises();
    }

    private void seedMuscleGroups() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM muscle_groups", Integer.class);
        if (count != null && count > 0) {
            log.info("muscle_groups already seeded ({} rows) - skipping", count);
            return;
        }
        Timestamp now = Timestamp.from(Instant.now());
        MgName[] names = MgName.values(); // CHEST..ABS, ids 1..12 preserve captured muscleGroupIds
        List<Object[]> rows = new ArrayList<>();
        for (int i = 0; i < names.length; i++) {
            rows.add(new Object[]{(long) (i + 1), names[i].name(), now, now});
        }
        jdbcTemplate.batchUpdate(
                "INSERT INTO muscle_groups (id, name, created_at, updated_at) VALUES (?, ?, ?, ?)",
                rows);
        log.info("Seeded {} muscle groups", rows.size());
    }

    private void seedExercises() throws Exception {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM exercises", Integer.class);
        if (count != null && count > 0) {
            log.info("exercises already seeded ({} rows) - skipping", count);
            return;
        }
        Timestamp now = Timestamp.from(Instant.now());
        JsonNode catalog;
        try (InputStream in = new ClassPathResource(EXERCISE_SEED).getInputStream()) {
            catalog = objectMapper.readTree(in);
        }

        List<Object[]> rows = new ArrayList<>();
        for (JsonNode ex : catalog) {
            rows.add(new Object[]{
                    ex.get("id").asLong(),
                    text(ex, "name"),
                    ex.hasNonNull("muscleGroupId") ? ex.get("muscleGroupId").asLong() : null,
                    text(ex, "youtubeId"),
                    enumName(text(ex, "exerciseType")),
                    now,
                    now,
                    enumName(text(ex, "mgSubType"))
            });
        }
        jdbcTemplate.batchUpdate(
                "INSERT INTO exercises (id, name, muscle_group_id, youtube_id, exercise_type, "
                        + "created_at, updated_at, mg_sub_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                rows);
        log.info("Seeded {} exercises", rows.size());
    }

    private static String text(JsonNode node, String field) {
        return node.hasNonNull(field) ? node.get(field).asText() : null;
    }

    /** Converts a serialized enum value (e.g. "bodyweight-only") to its enum constant name. */
    private static String enumName(String serialized) {
        if (serialized == null || serialized.isBlank()) {
            return null;
        }
        return serialized.replace('-', '_').toUpperCase();
    }
}
