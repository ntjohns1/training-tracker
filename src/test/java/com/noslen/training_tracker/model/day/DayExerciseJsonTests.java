package com.noslen.training_tracker.model.day;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
// 1. TODO
@JsonTest
public class DayExerciseJsonTests {

    @Autowired
    private JacksonTester<DayExercise> json;
    
    @Test
    public void dayExerciseSerializationTest() throws IOException {

        DayExercise dayExercise = DayExercise.builder()
                .id(121219691L)
                .dayId(19749541L)
                .exerciseId(51L)
                .position(0)
                .jointPain(0)
                .createdAt(Instant.parse("2025-06-12T00:44:33.064Z"))
                .updatedAt(Instant.parse("2025-06-21T16:51:05.289Z"))
                .sourceDayExerciseId(null)
                .muscleGroupId(2L)
                .status("complete")
                .sets(new ArrayList<>())
                .build();

        // Assert that the serialized DayExercise matches expected JSON structure
        assertThat(json.write(dayExercise)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(dayExercise)).extractingJsonPathNumberValue("@.id").isEqualTo(121219691);
        assertThat(json.write(dayExercise)).hasJsonPathNumberValue("@.dayId");
        assertThat(json.write(dayExercise)).extractingJsonPathNumberValue("@.dayId").isEqualTo(19749541);
        assertThat(json.write(dayExercise)).hasJsonPathNumberValue("@.exerciseId");
        assertThat(json.write(dayExercise)).extractingJsonPathNumberValue("@.exerciseId").isEqualTo(51);
        assertThat(json.write(dayExercise)).hasJsonPathNumberValue("@.position");
        assertThat(json.write(dayExercise)).extractingJsonPathNumberValue("@.position").isEqualTo(0);
        assertThat(json.write(dayExercise)).hasJsonPathNumberValue("@.jointPain");
        assertThat(json.write(dayExercise)).extractingJsonPathNumberValue("@.jointPain").isEqualTo(0);
        assertThat(json.write(dayExercise)).hasJsonPathStringValue("@.createdAt");
        assertThat(json.write(dayExercise)).hasJsonPathStringValue("@.updatedAt");
        assertThat(json.write(dayExercise)).hasJsonPathNumberValue("@.muscleGroupId");
        assertThat(json.write(dayExercise)).extractingJsonPathNumberValue("@.muscleGroupId").isEqualTo(2);
        assertThat(json.write(dayExercise)).hasJsonPathStringValue("@.status");
        assertThat(json.write(dayExercise)).extractingJsonPathStringValue("@.status").isEqualTo("complete");
        assertThat(json.write(dayExercise)).hasJsonPathArrayValue("@.sets");
    }
    
    @Test
    public void dayExerciseDeserializationTest() throws IOException {
        ClassPathResource resource = new ClassPathResource("example/day_exercise.json");
        DayExercise dayExercise = json.readObject(resource.getFile());

        // Assert that the DayExercise object has the expected properties
        assertThat(dayExercise.getId()).isEqualTo(121219682L);
        assertThat(dayExercise.getDayId()).isEqualTo(19749539L);
        assertThat(dayExercise.getExerciseId()).isEqualTo(161L);
        assertThat(dayExercise.getPosition()).isEqualTo(3);
        assertThat(dayExercise.getJointPain()).isEqualTo(0);
        assertThat(dayExercise.getCreatedAt()).isEqualTo(Instant.parse("2025-06-12T00:44:33.064Z"));
        assertThat(dayExercise.getUpdatedAt()).isEqualTo(Instant.parse("2025-06-18T20:24:42.341Z"));
        assertThat(dayExercise.getSourceDayExerciseId()).isNull();
        assertThat(dayExercise.getMuscleGroupId()).isEqualTo(6L);
        assertThat(dayExercise.getStatus()).isEqualTo("complete");
        assertThat(dayExercise.getSets()).isNotEmpty();
    }
}
