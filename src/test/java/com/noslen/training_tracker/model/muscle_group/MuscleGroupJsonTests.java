package com.noslen.training_tracker.model.muscle_group;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class MuscleGroupJsonTests {
    @Autowired
    private JacksonTester<MuscleGroup> json;

    @Test
    void testDeserialize() throws IOException {
        String jsonContent = "{"
                + "\"id\": 12,"
                + "\"name\": \"Quads\","
                + "\"createdAt\": \"2022-11-21T23:28:14.769Z\","
                + "\"updatedAt\": \"2022-11-21T23:28:15.342Z\""
                + "}";

        // Parse the JSON string into a MuscleGroup object
        MuscleGroup muscleGroup = json.parse(jsonContent).getObject();

        // Verify the parsed object has the expected values
        assertThat(muscleGroup.getId()).isEqualTo(12L);
        assertThat(muscleGroup.getName()).isEqualTo("Quads");
        assertThat(muscleGroup.getCreatedAt()).isEqualTo(Instant.parse("2022-11-21T23:28:14.769Z"));
        assertThat(muscleGroup.getUpdatedAt()).isEqualTo(Instant.parse("2022-11-21T23:28:15.342Z"));
    }
}
