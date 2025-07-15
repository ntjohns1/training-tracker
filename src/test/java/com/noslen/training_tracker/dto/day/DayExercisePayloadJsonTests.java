package com.noslen.training_tracker.dto.day;

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
public class DayExercisePayloadJsonTests {
    @Autowired
    private JacksonTester<DayExercisePayload> json;

    @Test
    void testSerialize() throws IOException {
        ArrayList<ExerciseSetPayload> sets = new ArrayList<>();
        ExerciseSetPayload exerciseSetPayload1 = new ExerciseSetPayload(
            156620877L, 
            121219682L, 
            0, 
            "regular", 
            115f, 
            null,
            null, 
            null, 
            7, 
            null, 
            null, 
            "lb", 
            Instant.parse("2025-06-12T00:44:33.064Z"),
            Instant.parse("2025-06-18T20:24:27.587Z"), 
            "complete");
        ExerciseSetPayload exerciseSetPayload2 = new ExerciseSetPayload(
            156620878L, 
            121219682L, 
            1, 
            "regular", 
            115f, 
            null,
            null, 
            null, 
            7, 
            null, 
            null, 
            "lb", 
            Instant.parse("2025-06-12T00:44:33.064Z"),
            Instant.parse("2025-06-18T20:24:36.532Z"), 
            "complete");
        sets.add(exerciseSetPayload1);
        sets.add(exerciseSetPayload2);
        DayExercisePayload dayExercisePayload = new DayExercisePayload(
            121219682L,
            19749539L,
            161L,
            3,
            0,
            Instant.parse("2025-06-12T00:44:33.064Z"),
            Instant.parse("2025-06-18T20:24:42.341Z"),
            null,
            6L,
            sets,
            "complete");
        ClassPathResource resource = new ClassPathResource("example/day_exercise.json");
        assertThat(json.write(dayExercisePayload)).isEqualToJson(resource);
    }
}
