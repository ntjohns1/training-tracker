package com.noslen.training_tracker.dto.exercise;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ExerciseNotePayloadJsonTests {
    @Autowired
    private JacksonTester<ExerciseNotePayload> json;

    @Test
    void testSerialize() throws IOException {
        ExerciseNotePayload exerciseNotePayload = new ExerciseNotePayload(
                1248357L,
                191L,
                1518614L,
                1304638L,
                121219737L,
                Instant.parse("2025-02-09T18:07:46.567Z"),
                Instant.parse("2025-06-29T18:39:37.789Z"),
                "Big chest all the way through");

        ClassPathResource resource = new ClassPathResource("example/exercise_note.json");
        assertThat(json.write(exerciseNotePayload)).isEqualToJson(resource);
    }

    @Test
    void testDeserialize() throws IOException {
        ClassPathResource resource = new ClassPathResource("example/exercise_note.json");
        ExerciseNotePayload exerciseNotePayload = json.readObject(resource.getFile());
        assertThat(exerciseNotePayload.id()).isEqualTo(1248357L);
        assertThat(exerciseNotePayload.exerciseId()).isEqualTo(191L);
        assertThat(exerciseNotePayload.userId()).isEqualTo(1518614L);
        assertThat(exerciseNotePayload.noteId()).isEqualTo(1304638L);
        assertThat(exerciseNotePayload.dayExerciseId()).isEqualTo(121219737L);
        assertThat(exerciseNotePayload.createdAt()).isEqualTo(Instant.parse("2025-02-09T18:07:46.567Z"));
        assertThat(exerciseNotePayload.updatedAt()).isEqualTo(Instant.parse("2025-06-29T18:39:37.789Z"));
        assertThat(exerciseNotePayload.text()).isEqualTo("Big chest all the way through");
    }
}
