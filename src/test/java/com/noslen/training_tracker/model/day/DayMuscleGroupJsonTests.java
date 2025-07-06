package com.noslen.training_tracker.model.day;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

// 3. TODO
@JsonTest
public class DayMuscleGroupJsonTests {

    @Autowired
    private JacksonTester<DayMuscleGroup> json;

    @Test
    public void dayMuscleGroupSerializationTest() throws IOException {
        // Create a DayMuscleGroup object with all necessary properties
        DayMuscleGroup dayMuscleGroup = DayMuscleGroup.builder()
                .id(89102468L)
                .dayId(19749552L)
                .muscleGroupId(2L)
                .pump(1)
                .soreness(0)
                .workload(1)
                .recommendedSets(5)
                .status("complete")
                .build();

        // Assert that the serialized DayMuscleGroup matches expected JSON structure
        assertThat(json.write(dayMuscleGroup)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(dayMuscleGroup)).extractingJsonPathNumberValue("@.id").isEqualTo(89102468);
        assertThat(json.write(dayMuscleGroup)).hasJsonPathNumberValue("@.dayId");
        assertThat(json.write(dayMuscleGroup)).extractingJsonPathNumberValue("@.dayId").isEqualTo(19749552);
        assertThat(json.write(dayMuscleGroup)).hasJsonPathNumberValue("@.muscleGroupId");
        assertThat(json.write(dayMuscleGroup)).extractingJsonPathNumberValue("@.muscleGroupId").isEqualTo(2);
        assertThat(json.write(dayMuscleGroup)).hasJsonPathNumberValue("@.pump");
        assertThat(json.write(dayMuscleGroup)).extractingJsonPathNumberValue("@.pump").isEqualTo(1);
        assertThat(json.write(dayMuscleGroup)).hasJsonPathNumberValue("@.soreness");
        assertThat(json.write(dayMuscleGroup)).extractingJsonPathNumberValue("@.soreness").isEqualTo(0);
        assertThat(json.write(dayMuscleGroup)).hasJsonPathNumberValue("@.workload");
        assertThat(json.write(dayMuscleGroup)).extractingJsonPathNumberValue("@.workload").isEqualTo(1);
        assertThat(json.write(dayMuscleGroup)).hasJsonPathNumberValue("@.recommendedSets");
        assertThat(json.write(dayMuscleGroup)).extractingJsonPathNumberValue("@.recommendedSets").isEqualTo(5);
        assertThat(json.write(dayMuscleGroup)).hasJsonPathStringValue("@.status");
        assertThat(json.write(dayMuscleGroup)).extractingJsonPathStringValue("@.status").isEqualTo("complete");
    }
}
