package com.noslen.training_tracker.model.day;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class DayExerciseJsonTests {

    @Autowired
    private JacksonTester<DayExercise> json;
    
    @Test
    public void dayExerciseSerializationTest() throws IOException {
        // Create a DayExercise object with all necessary properties
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
        // Create sample JSON content
        String jsonContent = "{"
                + "\"id\": 121219691,"
                + "\"dayId\": 19749541,"
                + "\"exerciseId\": 51,"
                + "\"position\": 0,"
                + "\"jointPain\": 0,"
                + "\"createdAt\": \"2025-06-12T00:44:33.064Z\","
                + "\"updatedAt\": \"2025-06-21T16:51:05.289Z\","
                + "\"sourceDayExerciseId\": null,"
                + "\"muscleGroupId\": 2,"
                + "\"status\": \"complete\","
                + "\"sets\": []"
                + "}";

        // Parse JSON into DayExercise object
        DayExercise dayExercise = json.parse(jsonContent).getObject();

        // Assert that the DayExercise object has the expected properties
        assertThat(dayExercise.getId()).isEqualTo(121219691L);
        assertThat(dayExercise.getDayId()).isEqualTo(19749541L);
        assertThat(dayExercise.getExerciseId()).isEqualTo(51L);
        assertThat(dayExercise.getPosition()).isEqualTo(0);
        assertThat(dayExercise.getJointPain()).isEqualTo(0);
        assertThat(dayExercise.getCreatedAt()).isEqualTo(Instant.parse("2025-06-12T00:44:33.064Z"));
        assertThat(dayExercise.getUpdatedAt()).isEqualTo(Instant.parse("2025-06-21T16:51:05.289Z"));
        assertThat(dayExercise.getSourceDayExerciseId()).isNull();
        assertThat(dayExercise.getMuscleGroupId()).isEqualTo(2L);
        assertThat(dayExercise.getStatus()).isEqualTo("complete");
        assertThat(dayExercise.getSets()).isEmpty();
    }
    
    @Test
    public void dayExerciseWithSetsDeserializationTest() throws IOException {
        // Create sample JSON content with sets
        String jsonContent = "{"
                + "\"id\": 121219691,"
                + "\"dayId\": 19749541,"
                + "\"exerciseId\": 51,"
                + "\"position\": 0,"
                + "\"jointPain\": 0,"
                + "\"createdAt\": \"2025-06-12T00:44:33.064Z\","
                + "\"updatedAt\": \"2025-06-21T16:51:05.289Z\","
                + "\"sourceDayExerciseId\": null,"
                + "\"muscleGroupId\": 2,"
                + "\"status\": \"complete\","
                + "\"sets\": ["
                + "  {"
                + "    \"id\": 157738019,"
                + "    \"dayExerciseId\": 121219691,"
                + "    \"position\": 0,"
                + "    \"setType\": \"regular\","
                + "    \"weight\": 35,"
                + "    \"reps\": 9,"
                + "    \"rir\": 2,"
                + "    \"intensity\": null,"
                + "    \"createdAt\": \"2025-06-12T00:44:33.064Z\","
                + "    \"updatedAt\": \"2025-06-21T16:51:05.289Z\""
                + "  },"
                + "  {"
                + "    \"id\": 157738020,"
                + "    \"dayExerciseId\": 121219691,"
                + "    \"position\": 1,"
                + "    \"setType\": \"regular\","
                + "    \"weight\": 35,"
                + "    \"reps\": 8,"
                + "    \"rir\": 1,"
                + "    \"intensity\": null,"
                + "    \"createdAt\": \"2025-06-12T00:44:33.064Z\","
                + "    \"updatedAt\": \"2025-06-21T16:51:05.289Z\""
                + "  }"
                + "]"
                + "}";

        // Parse JSON into DayExercise object
        DayExercise dayExercise = json.parse(jsonContent).getObject();

        // Assert that the DayExercise object has the expected properties
        assertThat(dayExercise.getId()).isEqualTo(121219691L);
        assertThat(dayExercise.getDayId()).isEqualTo(19749541L);
        assertThat(dayExercise.getExerciseId()).isEqualTo(51L);
        
        // Check sets
        assertThat(dayExercise.getSets()).isNotEmpty();
        assertThat(dayExercise.getSets().size()).isEqualTo(2);
        
        // First set checks
        var firstSet = dayExercise.getSets().get(0);
        assertThat(firstSet.getId()).isEqualTo(157738019L);
        assertThat(firstSet.getDayExerciseId()).isEqualTo(121219691L);
        assertThat(firstSet.getPosition()).isEqualTo(0);
        assertThat(firstSet.getSetType()).isEqualTo("regular");
        assertThat(firstSet.getWeight()).isEqualTo(35.0);
        assertThat(firstSet.getReps()).isEqualTo(9);
        assertThat(firstSet.getRir()).isEqualTo(2);
        
        // Second set checks
        var secondSet = dayExercise.getSets().get(1);
        assertThat(secondSet.getId()).isEqualTo(157738020L);
        assertThat(secondSet.getPosition()).isEqualTo(1);
        assertThat(secondSet.getReps()).isEqualTo(8);
        assertThat(secondSet.getRir()).isEqualTo(1);
    }
}
