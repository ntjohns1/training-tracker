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
public class MesocycleJsonTests {
    @Autowired
    private JacksonTester<Mesocycle> json;

    @Test
    void testSerialize() throws IOException {
        // Create a Mesocycle instance with minimal fields for serialization test
        Mesocycle mesocycle = Mesocycle.builder()
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
                .weeks(new ArrayList<>())
                .build();

        // Assert that the serialized JSON matches the expected format
        assertThat(json.write(mesocycle)).isEqualToJson("mesocycle.json");

        // Verify specific fields
        assertThat(json.write(mesocycle)).extractingJsonPathStringValue("$.key")
                .isEqualTo("wzzidovd6137");
        assertThat(json.write(mesocycle)).extractingJsonPathStringValue("$.name")
                .isEqualTo("2025 P6");
        assertThat(json.write(mesocycle)).extractingJsonPathNumberValue("$.days")
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

        // Parse the JSON string into a Mesocycle object
        Mesocycle mesocycle = json.parse(jsonContent).getObject();

        // Verify the parsed object has the expected values
        assertThat(mesocycle.getId()).isEqualTo(790173L);
        assertThat(mesocycle.getKey()).isEqualTo("wzzidovd6137");
        assertThat(mesocycle.getUserId()).isEqualTo(1518614L);
        assertThat(mesocycle.getName()).isEqualTo("2025 P6");
        assertThat(mesocycle.getDays()).isEqualTo(5);
        assertThat(mesocycle.getUnit()).isEqualTo("lb");
        assertThat(mesocycle.getSourceTemplateId()).isEqualTo(16909L);
        assertThat(mesocycle.getMicroRirs()).isEqualTo(32108L);
        assertThat(mesocycle.getCreatedAt()).isEqualTo(Instant.parse("2025-06-12T00:44:33.064Z"));
        assertThat(mesocycle.getUpdatedAt()).isEqualTo(Instant.parse("2025-07-05T16:02:18.167Z"));
    }
}
