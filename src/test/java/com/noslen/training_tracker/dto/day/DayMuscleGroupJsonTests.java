package com.noslen.training_tracker.dto.day;

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
    private JacksonTester<DayMuscleGroupPayload> json;

    @Test
    void testSerialize() throws IOException {
        DayMuscleGroupPayload dayMuscleGroupPayload = new DayMuscleGroupPayload(
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

        assertThat(json.write(dayMuscleGroupPayload)).hasJsonPathNumberValue("$.id");
        assertThat(json.write(dayMuscleGroupPayload)).extractingJsonPathNumberValue("$.id").isEqualTo(89102505);
        assertThat(json.write(dayMuscleGroupPayload)).hasJsonPathNumberValue("$.dayId");
        assertThat(json.write(dayMuscleGroupPayload)).extractingJsonPathNumberValue("$.dayId").isEqualTo(19749549);
        assertThat(json.write(dayMuscleGroupPayload)).hasJsonPathNumberValue("$.muscleGroupId");
        assertThat(json.write(dayMuscleGroupPayload)).extractingJsonPathNumberValue("$.muscleGroupId").isEqualTo(7);
        assertThat(json.write(dayMuscleGroupPayload)).hasJsonPathNumberValue("$.pump");
        assertThat(json.write(dayMuscleGroupPayload)).extractingJsonPathNumberValue("$.pump").isEqualTo(2);
        assertThat(json.write(dayMuscleGroupPayload)).hasJsonPathNumberValue("$.soreness");
        assertThat(json.write(dayMuscleGroupPayload)).extractingJsonPathNumberValue("$.soreness").isEqualTo(1);
        assertThat(json.write(dayMuscleGroupPayload)).hasJsonPathNumberValue("$.workload");
        assertThat(json.write(dayMuscleGroupPayload)).extractingJsonPathNumberValue("$.workload").isEqualTo(1);
        assertThat(json.write(dayMuscleGroupPayload)).hasJsonPathNumberValue("$.recommendedSets");
        assertThat(json.write(dayMuscleGroupPayload)).extractingJsonPathNumberValue("$.recommendedSets").isEqualTo(2);
        assertThat(json.write(dayMuscleGroupPayload)).hasJsonPathStringValue("$.status");
        assertThat(json.write(dayMuscleGroupPayload)).extractingJsonPathStringValue("$.status").isEqualTo("complete");
    }

    @Test
    void testDeserialize() throws IOException {
        ClassPathResource resource = new ClassPathResource("example/day_muscle_group.json");
        DayMuscleGroupPayload dayMuscleGroupPayload = json.readObject(resource.getFile());

        assertThat(dayMuscleGroupPayload.id()).isEqualTo(89102505L);
        assertThat(dayMuscleGroupPayload.dayId()).isEqualTo(19749549L);
        assertThat(dayMuscleGroupPayload.muscleGroupId()).isEqualTo(7L);
        assertThat(dayMuscleGroupPayload.pump()).isEqualTo(2);
        assertThat(dayMuscleGroupPayload.soreness()).isEqualTo(1);
        assertThat(dayMuscleGroupPayload.workload()).isEqualTo(1);
        assertThat(dayMuscleGroupPayload.recommendedSets()).isEqualTo(2);
        assertThat(dayMuscleGroupPayload.status()).isEqualTo("complete");
    }

}
