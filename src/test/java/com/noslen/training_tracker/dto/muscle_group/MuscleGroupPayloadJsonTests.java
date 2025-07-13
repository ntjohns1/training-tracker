package com.noslen.training_tracker.dto.muscle_group;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class MuscleGroupPayloadJsonTests {
    @Autowired
    private JacksonTester<MuscleGroupPayload> json;

    @Test
    void testSerialize() throws IOException {
        MuscleGroupPayload muscleGroupPayload = new MuscleGroupPayload(12L, "Quads", Instant.parse("2022-11-21T23:28:14.769Z"), Instant.parse("2022-11-21T23:28:15.342Z"));
        ClassPathResource resource = new ClassPathResource("example/muscle_group.json");
        assertThat(json.write(muscleGroupPayload)).isEqualToJson(resource);
    }

    @Test
    void testDeserialize() throws IOException {
        ClassPathResource resource = new ClassPathResource("example/muscle_group.json");
        MuscleGroupPayload muscleGroupPayload = json.readObject(resource.getFile());

        assertThat(muscleGroupPayload.id()).isEqualTo(12L);
        assertThat(muscleGroupPayload.name()).isEqualTo("Quads");
        assertThat(muscleGroupPayload.createdAt()).isEqualTo(Instant.parse("2022-11-21T23:28:14.769Z"));
        assertThat(muscleGroupPayload.updatedAt()).isEqualTo(Instant.parse("2022-11-21T23:28:15.342Z"));
    }
}
