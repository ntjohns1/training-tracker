package com.noslen.training_tracker.dto.mesocycle;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class MesocyclePayloadJsonTests {
    @Autowired
    private JacksonTester<MesocyclePayload> json;

    @Test
    void testSerialize() throws IOException {
        MesoNotePayload mesoNotePayload = new MesoNotePayload(
            13571L,
            790173L,
            1634147L,
            Instant.parse("2025-07-05T19:06:19.128Z"),
            Instant.parse("2025-07-05T19:06:19.128Z"),
            "For P7: \nBack to cable Lateral Raises");
        
        ArrayList<MesoNotePayload> notes = new ArrayList<MesoNotePayload>();
        notes.add(mesoNotePayload);
        
        MesocyclePayload mesocyclePayload = new MesocyclePayload(
            790173L,
            "wzzidovd6137",
            1518614L,
            "2025 P6",
            5,
            "lb",
            16909L,
            null,
            32108L,
            Instant.parse("2025-06-12T00:44:33.064Z"),
            Instant.parse("2025-07-05T16:02:18.167Z"),
            null,
            null,
            Instant.parse("2025-06-19T20:16:44.012Z"),
            Instant.parse("2025-06-13T20:19:45.802Z"),
            Instant.parse("2025-06-13T19:16:49.001Z"),
            Instant.parse("2025-06-13T19:16:44.810Z"),
            Instant.parse("2025-07-02T13:49:49.037Z"),
            Instant.parse("2025-07-05T16:00:02.611Z"),
            null,
            Instant.parse("2025-07-05T16:02:18.077Z"),
            Instant.parse("2025-07-05T16:02:18.077Z"),
            null,
            null,
            5,
            notes);

        ClassPathResource resource = new ClassPathResource("example/mesocycle.json");
        assertThat(json.write(mesocyclePayload)).isEqualToJson(resource);
    }

    @Test
    void testDeserialize() throws IOException {
        ClassPathResource resource = new ClassPathResource("example/mesocycle.json");
        MesocyclePayload mesocyclePayload = json.readObject(resource.getFile());
        
        assertThat(mesocyclePayload.id()).isEqualTo(790173L);
        assertThat(mesocyclePayload.key()).isEqualTo("wzzidovd6137");
        assertThat(mesocyclePayload.userId()).isEqualTo(1518614L);
        assertThat(mesocyclePayload.name()).isEqualTo("2025 P6");
        assertThat(mesocyclePayload.days()).isEqualTo(5);
        assertThat(mesocyclePayload.unit()).isEqualTo("lb");
        assertThat(mesocyclePayload.sourceTemplateId()).isEqualTo(16909L);
        assertThat(mesocyclePayload.sourceMesoId()).isNull();
        assertThat(mesocyclePayload.microRirs()).isEqualTo(32108L);
        assertThat(mesocyclePayload.createdAt()).isEqualTo(Instant.parse("2025-06-12T00:44:33.064Z"));
        assertThat(mesocyclePayload.updatedAt()).isEqualTo(Instant.parse("2025-07-05T16:02:18.167Z"));
        assertThat(mesocyclePayload.finishedAt()).isNull();
        assertThat(mesocyclePayload.deletedAt()).isNull();
        assertThat(mesocyclePayload.firstMicroCompletedAt()).isEqualTo(Instant.parse("2025-06-19T20:16:44.012Z"));
        assertThat(mesocyclePayload.firstWorkoutCompletedAt()).isEqualTo(Instant.parse("2025-06-13T20:19:45.802Z"));
        assertThat(mesocyclePayload.firstExerciseCompletedAt()).isEqualTo(Instant.parse("2025-06-13T19:16:49.001Z"));
        assertThat(mesocyclePayload.firstSetCompletedAt()).isEqualTo(Instant.parse("2025-06-13T19:16:44.810Z"));
        assertThat(mesocyclePayload.lastMicroFinishedAt()).isEqualTo(Instant.parse("2025-07-02T13:49:49.037Z"));
        assertThat(mesocyclePayload.lastSetCompletedAt()).isEqualTo(Instant.parse("2025-07-05T16:00:02.611Z"));
        assertThat(mesocyclePayload.lastSetSkippedAt()).isNull();
        assertThat(mesocyclePayload.lastWorkoutCompletedAt()).isEqualTo(Instant.parse("2025-07-05T16:02:18.077Z"));
        assertThat(mesocyclePayload.lastWorkoutFinishedAt()).isEqualTo(Instant.parse("2025-07-05T16:02:18.077Z"));
        assertThat(mesocyclePayload.lastWorkoutSkippedAt()).isNull();
        assertThat(mesocyclePayload.lastWorkoutPartialedAt()).isNull();
        assertThat(mesocyclePayload.weeks()).isEqualTo(5);
        assertThat(mesocyclePayload.notes()).hasSize(1);
    }
}
