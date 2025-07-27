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
public class ExerciseNoteResponseJsonTests {
    @Autowired
    private JacksonTester<ExerciseNoteResponse> json;

    @Test
    void testSerialize() throws IOException {
        ExerciseNoteResponse exerciseNoteResponse = new ExerciseNoteResponse(
                1248357L,
                191L,
                1518614L,
                1304638L,
                121219737L,
                Instant.parse("2025-02-09T18:07:46.567Z"),
                Instant.parse("2025-06-29T18:39:37.789Z"),
                "Big chest all the way through");

        ClassPathResource resource = new ClassPathResource("example/exercise_note.json");
        assertThat(json.write(exerciseNoteResponse)).isEqualToJson(resource);
    }

    @Test
    void testDeserialize() throws IOException {
        ClassPathResource resource = new ClassPathResource("example/exercise_note.json");
        ExerciseNoteResponse exerciseNoteResponse = json.readObject(resource.getFile());
        assertThat(exerciseNoteResponse.id()).isEqualTo(1248357L);
        assertThat(exerciseNoteResponse.exerciseId()).isEqualTo(191L);
        assertThat(exerciseNoteResponse.userId()).isEqualTo(1518614L);
        assertThat(exerciseNoteResponse.noteId()).isEqualTo(1304638L);
        assertThat(exerciseNoteResponse.dayExerciseId()).isEqualTo(121219737L);
        assertThat(exerciseNoteResponse.createdAt()).isEqualTo(Instant.parse("2025-02-09T18:07:46.567Z"));
        assertThat(exerciseNoteResponse.updatedAt()).isEqualTo(Instant.parse("2025-06-29T18:39:37.789Z"));
        assertThat(exerciseNoteResponse.text()).isEqualTo("Big chest all the way through");
    }
}
