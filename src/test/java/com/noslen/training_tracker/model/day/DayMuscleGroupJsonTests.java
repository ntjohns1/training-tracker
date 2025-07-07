package com.noslen.training_tracker.model.day;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class DayMuscleGroupJsonTests {

    @Autowired
    private JacksonTester<DayMuscleGroup> json;

    @Test
    public void dayMuscleGroupSerializationTest() throws IOException {

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

    @Test
    public void dayMuscleGroupDeserializationTest() throws IOException {

        ClassPathResource resource = new ClassPathResource("example/day_muscle_group.json");
        DayMuscleGroup dayMuscleGroup = json.readObject(resource.getFile());

        assertThat(dayMuscleGroup.getId()).isEqualTo(89102505L);
        assertThat(dayMuscleGroup.getDayId()).isEqualTo(19749549L);
        assertThat(dayMuscleGroup.getMuscleGroupId()).isEqualTo(7L);
        assertThat(dayMuscleGroup.getPump()).isEqualTo(2);
        assertThat(dayMuscleGroup.getSoreness()).isEqualTo(1);
        assertThat(dayMuscleGroup.getWorkload()).isEqualTo(1);
        assertThat(dayMuscleGroup.getRecommendedSets()).isEqualTo(2);   
        assertThat(dayMuscleGroup.getStatus()).isEqualTo("complete");

    }
}
