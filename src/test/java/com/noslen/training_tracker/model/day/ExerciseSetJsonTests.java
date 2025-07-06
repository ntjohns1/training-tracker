package com.noslen.training_tracker.model.day;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ExerciseSetJsonTests {

    @Autowired
    private JacksonTester<ExerciseSet> json;
    
    @Test
    public void exerciseSetSerializationTest() throws IOException {
        // Create an ExerciseSet object with all necessary properties
        ExerciseSet exerciseSet = ExerciseSet.builder()
                .id(157738019L)
                .dayExerciseId(121219691L)
                .position(0)
                .setType("regular")
                .weight(35.0)
                .reps(9)
                .rir(2)
                .intensity(null)
                .createdAt(Instant.parse("2025-06-12T00:44:33.064Z"))
                .updatedAt(Instant.parse("2025-06-21T16:51:05.289Z"))
                .build();

        // Assert that the serialized ExerciseSet matches expected JSON structure
        assertThat(json.write(exerciseSet)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(exerciseSet)).extractingJsonPathNumberValue("@.id").isEqualTo(157738019);
        assertThat(json.write(exerciseSet)).hasJsonPathNumberValue("@.dayExerciseId");
        assertThat(json.write(exerciseSet)).extractingJsonPathNumberValue("@.dayExerciseId").isEqualTo(121219691);
        assertThat(json.write(exerciseSet)).hasJsonPathNumberValue("@.position");
        assertThat(json.write(exerciseSet)).extractingJsonPathNumberValue("@.position").isEqualTo(0);
        assertThat(json.write(exerciseSet)).hasJsonPathStringValue("@.setType");
        assertThat(json.write(exerciseSet)).extractingJsonPathStringValue("@.setType").isEqualTo("regular");
        assertThat(json.write(exerciseSet)).hasJsonPathNumberValue("@.weight");
        assertThat(json.write(exerciseSet)).extractingJsonPathNumberValue("@.weight").isEqualTo(35.0);
        assertThat(json.write(exerciseSet)).hasJsonPathNumberValue("@.reps");
        assertThat(json.write(exerciseSet)).extractingJsonPathNumberValue("@.reps").isEqualTo(9);
        assertThat(json.write(exerciseSet)).hasJsonPathNumberValue("@.rir");
        assertThat(json.write(exerciseSet)).extractingJsonPathNumberValue("@.rir").isEqualTo(2);
        assertThat(json.write(exerciseSet)).hasJsonPathStringValue("@.createdAt");
        assertThat(json.write(exerciseSet)).hasJsonPathStringValue("@.updatedAt");
    }
    
    @Test
    public void exerciseSetDeserializationTest() throws IOException {
        // Create sample JSON content
        String jsonContent = "{"
                + "\"id\": 157738019,"
                + "\"dayExerciseId\": 121219691,"
                + "\"position\": 0,"
                + "\"setType\": \"regular\","
                + "\"weight\": 35,"
                + "\"reps\": 9,"
                + "\"rir\": 2,"
                + "\"intensity\": null,"
                + "\"createdAt\": \"2025-06-12T00:44:33.064Z\","
                + "\"updatedAt\": \"2025-06-21T16:51:05.289Z\""
                + "}";

        // Parse JSON into ExerciseSet object
        ExerciseSet exerciseSet = json.parse(jsonContent).getObject();

        // Assert that the ExerciseSet object has the expected properties
        assertThat(exerciseSet.getId()).isEqualTo(157738019L);
        assertThat(exerciseSet.getDayExerciseId()).isEqualTo(121219691L);
        assertThat(exerciseSet.getPosition()).isEqualTo(0);
        assertThat(exerciseSet.getSetType()).isEqualTo("regular");
        assertThat(exerciseSet.getWeight()).isEqualTo(35.0);
        assertThat(exerciseSet.getReps()).isEqualTo(9);
        assertThat(exerciseSet.getRir()).isEqualTo(2);
        assertThat(exerciseSet.getIntensity()).isNull();
        assertThat(exerciseSet.getCreatedAt()).isEqualTo(Instant.parse("2025-06-12T00:44:33.064Z"));
        assertThat(exerciseSet.getUpdatedAt()).isEqualTo(Instant.parse("2025-06-21T16:51:05.289Z"));
    }
}
