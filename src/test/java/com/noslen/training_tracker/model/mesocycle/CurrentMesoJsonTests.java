package com.noslen.training_tracker.model.mesocycle;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CurrentMesoJsonTests {
    @Autowired
    private JacksonTester<CurrentMeso> json;

    @Test
    void testSerialize() throws IOException {
        // Create a CurrentMeso instance with minimal fields for serialization test
        CurrentMeso currentMeso = CurrentMeso.builder()
                .id(790173L)
                .key("wzzidovd6137")
                .userId(1518614L)
                .name("2025 P6")
                .days(5)
                .unit("lb")
                .sourceTemplateId(16909L)
                .microRirs(32108L)
                .createdAt(Instant.parse("2025-06-12T00:44:33.064Z"))
                .updatedAt(Instant.parse("2025-07-05T16:02:18.167Z"))
                .firstMicroCompletedAt(Instant.parse("2025-06-19T20:16:44.012Z"))
                .firstWorkoutCompletedAt(Instant.parse("2025-06-13T20:19:45.802Z"))
                .firstExerciseCompletedAt(Instant.parse("2025-06-13T19:16:49.001Z"))
                .firstSetCompletedAt(Instant.parse("2025-06-13T19:16:44.810Z"))
                .lastMicroFinishedAt(Instant.parse("2025-07-02T13:49:49.037Z"))
                .lastSetCompletedAt(Instant.parse("2025-07-05T16:00:02.611Z"))
                .lastWorkoutCompletedAt(Instant.parse("2025-07-05T16:02:18.077Z"))
                .lastWorkoutFinishedAt(Instant.parse("2025-07-05T16:02:18.077Z"))
                .weeks(new ArrayList<>())
                .build();

        // Assert that the serialized JSON matches the expected format
        assertThat(json.write(currentMeso)).isEqualToJson("current_meso.json");

        // Verify specific fields
        assertThat(json.write(currentMeso)).extractingJsonPathStringValue("$.key")
                .isEqualTo("wzzidovd6137");
        assertThat(json.write(currentMeso)).extractingJsonPathStringValue("$.name")
                .isEqualTo("2025 P6");
        assertThat(json.write(currentMeso)).extractingJsonPathNumberValue("$.days")
                .isEqualTo(5);
    }

    @Test
    void testDeserialize() throws IOException {
        String jsonContent = "{"
                + "\"id\": 790173,"
                + "\"key\": \"wzzidovd6137\","
                + "\"userId\": 1518614,"
                + "\"name\": \"2025 P6\","
                + "\"days\": 5,"
                + "\"unit\": \"lb\","
                + "\"sourceTemplateId\": 16909,"
                + "\"sourceMesoId\": null,"
                + "\"microRirs\": 32108,"
                + "\"createdAt\": \"2025-06-12T00:44:33.064Z\","
                + "\"updatedAt\": \"2025-07-05T16:02:18.167Z\","
                + "\"weeks\": []"
                + "}";

        // Parse the JSON string into a CurrentMeso object
        CurrentMeso currentMeso = json.parse(jsonContent).getObject();

        // Verify the parsed object has the expected values
        assertThat(currentMeso.getId()).isEqualTo(790173L);
        assertThat(currentMeso.getKey()).isEqualTo("wzzidovd6137");
        assertThat(currentMeso.getUserId()).isEqualTo(1518614L);
        assertThat(currentMeso.getName()).isEqualTo("2025 P6");
        assertThat(currentMeso.getDays()).isEqualTo(5);
        assertThat(currentMeso.getUnit()).isEqualTo("lb");
        assertThat(currentMeso.getSourceTemplateId()).isEqualTo(16909L);
        assertThat(currentMeso.getMicroRirs()).isEqualTo(32108L);
        assertThat(currentMeso.getCreatedAt()).isEqualTo(Instant.parse("2025-06-12T00:44:33.064Z"));
        assertThat(currentMeso.getUpdatedAt()).isEqualTo(Instant.parse("2025-07-05T16:02:18.167Z"));
    }
}
