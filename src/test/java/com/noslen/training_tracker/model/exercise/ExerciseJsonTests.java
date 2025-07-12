package com.noslen.training_tracker.model.exercise;

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
public class ExerciseJsonTests {

    @Autowired
    private JacksonTester<Exercise> json;

    @Test
    public void exerciseSerializationTest() throws IOException {
        Exercise exercise = Exercise.builder()
                .id(221L)
                .name("Hanging Straight Leg Raise")
                .muscleGroupId(12L)
                .youtubeId("45v5678dA6BC")
                .exerciseType("bodyweight-only")
                .createdAt(Instant.parse("2022-11-21T23:28:14.769Z"))
                .updatedAt(Instant.parse("2022-11-21T23:28:15.342Z"))
                .notes(new ArrayList<>())
                .build();

        ExerciseNote note = ExerciseNote.builder()
                .id(1319238L)
                .exerciseId(221L)
                .userId(1518614L)
                .noteId(1378654L)
                .dayExerciseId(92915187L)
                .createdAt(Instant.parse("2025-03-11T13:27:46.588Z"))
                .updatedAt(Instant.parse("2025-03-11T13:29:15.384Z"))
                .text("Knees at 110 degrees")
                .build();

        exercise.getNotes().add(note);

        ClassPathResource resource = new ClassPathResource("example/exercise.json");
        assertThat(json.write(exercise)).isEqualToJson(resource);

        assertThat(json.write(exercise)).extractingJsonPathNumberValue("@.id").isEqualTo(221);
        assertThat(json.write(exercise)).extractingJsonPathStringValue("@.name")
                .isEqualTo("Hanging Straight Leg Raise");
        assertThat(json.write(exercise)).extractingJsonPathNumberValue("@.muscleGroupId").isEqualTo(12);
        assertThat(json.write(exercise)).extractingJsonPathStringValue("@.youtubeId").isEqualTo("45v5678dA6BC");
        assertThat(json.write(exercise)).extractingJsonPathStringValue("@.exerciseType").isEqualTo("bodyweight-only");
        assertThat(json.write(exercise)).extractingJsonPathNumberValue("@.userId").isNull();
        assertThat(json.write(exercise)).extractingJsonPathValue("@.createdAt").isEqualTo("2022-11-21T23:28:14.769Z");
        assertThat(json.write(exercise)).extractingJsonPathValue("@.updatedAt").isEqualTo("2022-11-21T23:28:15.342Z");
        assertThat(json.write(exercise)).extractingJsonPathNumberValue("@.deletedAt").isNull();
        assertThat(json.write(exercise)).extractingJsonPathStringValue("@.mgSubType").isNull();
        assertThat(json.write(exercise)).hasJsonPath("@.notes");
    }
//    @TODO: Refactor using DTOs
//    @Test
//    public void exerciseDeserializationTest() throws IOException {
//        ClassPathResource resource = new ClassPathResource("example/exercise.json");
//        Exercise exercise = json.readObject(resource.getFile());
//
//        assertThat(exercise.getId()).isEqualTo(221L);
//        assertThat(exercise.getName()).isEqualTo("Hanging Straight Leg Raise");
//        assertThat(exercise.getMuscleGroupId()).isEqualTo(12);
//        assertThat(exercise.getYoutubeId()).isEqualTo("45v5678dA6BC");
//        assertThat(exercise.getExerciseType()).isEqualTo("bodyweight-only");
//        assertThat(exercise.getUserId()).isNull();
//        assertThat(exercise.getCreatedAt()).isEqualTo(Instant.parse("2022-11-21T23:28:14.769Z"));
//        assertThat(exercise.getUpdatedAt()).isEqualTo(Instant.parse("2022-11-21T23:28:15.342Z"));
//        assertThat(exercise.getDeletedAt()).isNull();
//        assertThat(exercise.getMgSubType()).isNull();
//        assertThat(exercise.getNotes()).isNotEmpty();
//    }
}
