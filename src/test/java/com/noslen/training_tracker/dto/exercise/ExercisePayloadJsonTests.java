package com.noslen.training_tracker.dto.exercise;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ExercisePayloadJsonTests {
    @Autowired
    private JacksonTester<ExercisePayload> json;

    @Test
    void testSerialize() throws IOException {
        List<ExerciseNotePayload> notes = new ArrayList<>();
        notes.add(new ExerciseNotePayload(
                1319238L,
                221L,
                1518614L,
                1378654L,
                92915187L,
                Instant.parse("2025-03-11T13:27:46.588Z"),
                Instant.parse("2025-03-11T13:29:15.384Z"),
                "Knees at 110 degrees"));
        ExercisePayload exercisePayload = new ExercisePayload(
                221L,
                "Hanging Straight Leg Raise",
                12L,
                "45v5678dA6BC",
                "bodyweight-only",
                null,
                Instant.parse("2022-11-21T23:28:14.769Z"),
                Instant.parse("2022-11-21T23:28:15.342Z"),
                null,
                null,
                notes);

        ClassPathResource resource = new ClassPathResource("example/exercise.json");
        assertThat(json.write(exercisePayload)).isEqualToJson(resource);
    }

    @Test
    void testDeserialize() throws IOException {
        ClassPathResource resource = new ClassPathResource("example/exercise.json");
        ExercisePayload exercisePayload = json.readObject(resource.getFile());
        assertThat(exercisePayload.id()).isEqualTo(221L);
        assertThat(exercisePayload.name()).isEqualTo("Hanging Straight Leg Raise");
        assertThat(exercisePayload.muscleGroupId()).isEqualTo(12L);
        assertThat(exercisePayload.youtubeId()).isEqualTo("45v5678dA6BC");
        assertThat(exercisePayload.exerciseType()).isEqualTo("bodyweight-only");
        assertThat(exercisePayload.userId()).isNull();
        assertThat(exercisePayload.createdAt()).isEqualTo(Instant.parse("2022-11-21T23:28:14.769Z"));
        assertThat(exercisePayload.updatedAt()).isEqualTo(Instant.parse("2022-11-21T23:28:15.342Z"));
        assertThat(exercisePayload.deletedAt()).isNull();
        assertThat(exercisePayload.mgSubType()).isNull();
        assertThat(exercisePayload.notes().get(0).id()).isEqualTo(1319238L);
        assertThat(exercisePayload.notes().get(0).exerciseId()).isEqualTo(221L);
        assertThat(exercisePayload.notes().get(0).userId()).isEqualTo(1518614L);
        assertThat(exercisePayload.notes().get(0).noteId()).isEqualTo(1378654L);
        assertThat(exercisePayload.notes().get(0).dayExerciseId()).isEqualTo(92915187L);
        assertThat(exercisePayload.notes().get(0).createdAt()).isEqualTo(Instant.parse("2025-03-11T13:27:46.588Z"));
        assertThat(exercisePayload.notes().get(0).updatedAt()).isEqualTo(Instant.parse("2025-03-11T13:29:15.384Z"));
        assertThat(exercisePayload.notes().get(0).text()).isEqualTo("Knees at 110 degrees");
    }
}
