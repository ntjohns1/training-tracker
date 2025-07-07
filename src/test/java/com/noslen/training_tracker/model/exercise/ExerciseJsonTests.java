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
                .mgSubType("vertical")
                .notes(new ArrayList<>())
                .build();

        assertThat(json.write(exercise)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(exercise)).extractingJsonPathNumberValue("@.id").isEqualTo(221);
        assertThat(json.write(exercise)).hasJsonPathStringValue("@.name");
        assertThat(json.write(exercise)).extractingJsonPathStringValue("@.name")
                .isEqualTo("Hanging Straight Leg Raise");
        assertThat(json.write(exercise)).hasJsonPathNumberValue("@.muscleGroupId");
        assertThat(json.write(exercise)).extractingJsonPathNumberValue("@.muscleGroupId").isEqualTo(12);
        assertThat(json.write(exercise)).hasJsonPathStringValue("@.youtubeId");
        assertThat(json.write(exercise)).extractingJsonPathStringValue("@.youtubeId").isEqualTo("45v5678dA6BC");
        assertThat(json.write(exercise)).hasJsonPathStringValue("@.exerciseType");
        assertThat(json.write(exercise)).extractingJsonPathStringValue("@.exerciseType").isEqualTo("bodyweight-only");
        assertThat(json.write(exercise)).hasJsonPath("@.userId");
        assertThat(json.write(exercise)).hasJsonPathValue("@.createdAt");
        assertThat(json.write(exercise)).hasJsonPathValue("@.updatedAt");
        assertThat(json.write(exercise)).hasJsonPath("@.deletedAt");
        assertThat(json.write(exercise)).hasJsonPath("@.mgSubType");
        assertThat(json.write(exercise)).hasJsonPathArrayValue("@.notes");
    }

    @Test
    public void exerciseDeserializationTest() throws IOException {
        ClassPathResource resource = new ClassPathResource("example/exercise.json");
        Exercise exercise = json.readObject(resource.getFile());

        assertThat(exercise.getId()).isEqualTo(221L);
        assertThat(exercise.getName()).isEqualTo("Hanging Straight Leg Raise");
        assertThat(exercise.getMuscleGroupId()).isEqualTo(12);
        assertThat(exercise.getYoutubeId()).isEqualTo("45v5678dA6BC");
        assertThat(exercise.getExerciseType()).isEqualTo("bodyweight-only");
        assertThat(exercise.getUserId()).isNull();
        assertThat(exercise.getCreatedAt()).isEqualTo(Instant.parse("2022-11-21T23:28:14.769Z"));
        assertThat(exercise.getUpdatedAt()).isEqualTo(Instant.parse("2022-11-21T23:28:15.342Z"));
        assertThat(exercise.getDeletedAt()).isNull();
        assertThat(exercise.getMgSubType()).isNull();
        assertThat(exercise.getNotes()).isNotEmpty();
    }
}
