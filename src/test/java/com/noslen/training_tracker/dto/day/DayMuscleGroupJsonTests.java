package com.noslen.training_tracker.dto.day;

import com.noslen.training_tracker.dto.day.response.DayMuscleGroupResponse;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class DayMuscleGroupJsonTests {
    @Autowired
    private JacksonTester<DayMuscleGroupResponse> json;

    @Test
    void testSerialize() throws IOException {
        DayMuscleGroupResponse dayMuscleGroupResponse = new DayMuscleGroupResponse(
                89102505L,
                19749549L,
                7L,
                2,
                1,
                1,
                Instant.parse("2025-06-12T00:44:33.064Z"),
                Instant.parse("2025-07-01T16:02:12.345Z"),
                2,
                "complete");

        assertThat(json.write(dayMuscleGroupResponse)).hasJsonPathNumberValue("$.id");
        assertThat(json.write(dayMuscleGroupResponse)).extractingJsonPathNumberValue("$.id").isEqualTo(89102505);
        assertThat(json.write(dayMuscleGroupResponse)).hasJsonPathNumberValue("$.dayId");
        assertThat(json.write(dayMuscleGroupResponse)).extractingJsonPathNumberValue("$.dayId").isEqualTo(19749549);
        assertThat(json.write(dayMuscleGroupResponse)).hasJsonPathNumberValue("$.muscleGroupId");
        assertThat(json.write(dayMuscleGroupResponse)).extractingJsonPathNumberValue("$.muscleGroupId").isEqualTo(7);
        assertThat(json.write(dayMuscleGroupResponse)).hasJsonPathNumberValue("$.pump");
        assertThat(json.write(dayMuscleGroupResponse)).extractingJsonPathNumberValue("$.pump").isEqualTo(2);
        assertThat(json.write(dayMuscleGroupResponse)).hasJsonPathNumberValue("$.soreness");
        assertThat(json.write(dayMuscleGroupResponse)).extractingJsonPathNumberValue("$.soreness").isEqualTo(1);
        assertThat(json.write(dayMuscleGroupResponse)).hasJsonPathNumberValue("$.workload");
        assertThat(json.write(dayMuscleGroupResponse)).extractingJsonPathNumberValue("$.workload").isEqualTo(1);
        assertThat(json.write(dayMuscleGroupResponse)).hasJsonPathNumberValue("$.recommendedSets");
        assertThat(json.write(dayMuscleGroupResponse)).extractingJsonPathNumberValue("$.recommendedSets").isEqualTo(2);
        assertThat(json.write(dayMuscleGroupResponse)).hasJsonPathStringValue("$.status");
        assertThat(json.write(dayMuscleGroupResponse)).extractingJsonPathStringValue("$.status").isEqualTo("complete");
    }

    @Test
    void testDeserialize() throws IOException {
        ClassPathResource resource = new ClassPathResource("example/day_muscle_group.json");
        DayMuscleGroupResponse dayMuscleGroupResponse = json.readObject(resource.getFile());

        assertThat(dayMuscleGroupResponse.id()).isEqualTo(89102505L);
        assertThat(dayMuscleGroupResponse.dayId()).isEqualTo(19749549L);
        assertThat(dayMuscleGroupResponse.muscleGroupId()).isEqualTo(7L);
        assertThat(dayMuscleGroupResponse.pump()).isEqualTo(2);
        assertThat(dayMuscleGroupResponse.soreness()).isEqualTo(1);
        assertThat(dayMuscleGroupResponse.workload()).isEqualTo(1);
        assertThat(dayMuscleGroupResponse.recommendedSets()).isEqualTo(2);
        assertThat(dayMuscleGroupResponse.status()).isEqualTo("complete");
    }

}
