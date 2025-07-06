package com.noslen.training_tracker.model.exercise;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
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
                .youtubeId("7FwGZ8qY5OU")
                .exerciseType("bodyweight-only")
                .userId(null)
                .createdAt(Instant.parse("2022-11-21T23:28:14.769Z"))
                .updatedAt(Instant.parse("2022-11-21T23:28:15.342Z"))
                .deletedAt(null)
                .mgSubType(null)
                .notes(new ArrayList<>())
                .build();

        // Assert that the serialized Exercise matches expected JSON structure
        assertThat(json.write(exercise)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(exercise)).extractingJsonPathNumberValue("@.id").isEqualTo(221);
        assertThat(json.write(exercise)).hasJsonPathStringValue("@.name");
        assertThat(json.write(exercise)).extractingJsonPathStringValue("@.name").isEqualTo("Hanging Straight Leg Raise");
        assertThat(json.write(exercise)).hasJsonPathNumberValue("@.muscleGroupId");
        assertThat(json.write(exercise)).extractingJsonPathNumberValue("@.muscleGroupId").isEqualTo(12);
        assertThat(json.write(exercise)).hasJsonPathStringValue("@.youtubeId");
        assertThat(json.write(exercise)).extractingJsonPathStringValue("@.youtubeId").isEqualTo("7FwGZ8qY5OU");
        assertThat(json.write(exercise)).hasJsonPathStringValue("@.exerciseType");
        assertThat(json.write(exercise)).extractingJsonPathStringValue("@.exerciseType").isEqualTo("bodyweight-only");
        assertThat(json.write(exercise)).hasJsonPathNumberValue("@.userId");
        assertThat(json.write(exercise)).extractingJsonPathNumberValue("@.userId").isNull();
        assertThat(json.write(exercise)).hasJsonPathStringValue("@.createdAt");
        assertThat(json.write(exercise)).hasJsonPathStringValue("@.updatedAt");
        assertThat(json.write(exercise)).hasJsonPathValue("@.deletedAt");
        assertThat(json.write(exercise)).hasJsonPathValue("@.mgSubType");
        assertThat(json.write(exercise)).hasJsonPathArrayValue("@.notes");
    }
}
