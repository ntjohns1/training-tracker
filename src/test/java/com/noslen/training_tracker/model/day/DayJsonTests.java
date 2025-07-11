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

@JsonTest
public class DayJsonTests {

    @Autowired
    private JacksonTester<Day> json;

    @Test
    public void daySerializationTest() throws IOException {

        Day day = Day.builder()
                .id(19749541L)
                .mesoId(790173L)
                .week(1)
                .position(0)
                .createdAt(Instant.parse("2025-06-12T00:44:33.064Z"))
                .updatedAt(Instant.parse("2025-06-21T16:51:05.289Z"))
                .bodyweight(161.0)
                .bodyweightAt(Instant.parse("2025-06-12T00:48:59.703Z"))
                .unit("lb")
                .finishedAt(Instant.parse("2025-06-21T16:51:05.182Z"))
                .label(null)
                .notes(new ArrayList<>())
                .exercises(new ArrayList<>())
                .muscleGroups(new ArrayList<>())
                .build();

        assertThat(json.write(day)).hasJsonPathNumberValue("@.id");
        assertThat(json.write(day)).extractingJsonPathNumberValue("@.id").isEqualTo(19749541);
        assertThat(json.write(day)).hasJsonPathNumberValue("@.mesoId");
        assertThat(json.write(day)).extractingJsonPathNumberValue("@.mesoId").isEqualTo(790173);
        assertThat(json.write(day)).hasJsonPathNumberValue("@.week");
        assertThat(json.write(day)).extractingJsonPathNumberValue("@.week").isEqualTo(1);
        assertThat(json.write(day)).hasJsonPathNumberValue("@.position");
        assertThat(json.write(day)).extractingJsonPathNumberValue("@.position").isEqualTo(0);
        assertThat(json.write(day)).hasJsonPathStringValue("@.createdAt");
        assertThat(json.write(day)).hasJsonPathStringValue("@.updatedAt");
        assertThat(json.write(day)).hasJsonPathNumberValue("@.bodyweight");
        assertThat(json.write(day)).extractingJsonPathNumberValue("@.bodyweight").isEqualTo(161.0);
        assertThat(json.write(day)).hasJsonPathStringValue("@.bodyweightAt");
        assertThat(json.write(day)).hasJsonPathStringValue("@.unit");
        assertThat(json.write(day)).extractingJsonPathStringValue("@.unit").isEqualTo("lb");
        assertThat(json.write(day)).hasJsonPathStringValue("@.finishedAt");
        assertThat(json.write(day)).hasJsonPathValue("@.notes");
        assertThat(json.write(day)).hasJsonPathArrayValue("@.notes");
    }

    @Test
    public void dayDeserializationTest() throws IOException {

        ClassPathResource resource = new ClassPathResource("example/day.json");
        Day day = json.readObject(resource.getFile());

        assertThat(day.getId()).isEqualTo(19749541L);
        assertThat(day.getMesoId()).isEqualTo(790173L);
        assertThat(day.getWeek()).isEqualTo(1);
        assertThat(day.getPosition()).isEqualTo(0);
        assertThat(day.getCreatedAt()).isEqualTo(Instant.parse("2025-06-12T00:44:33.064Z"));
        assertThat(day.getUpdatedAt()).isEqualTo(Instant.parse("2025-06-21T16:51:05.289Z"));
        assertThat(day.getBodyweight()).isEqualTo(161.0);
        assertThat(day.getBodyweightAt()).isEqualTo(Instant.parse("2025-06-12T00:48:59.703Z"));
        assertThat(day.getUnit()).isEqualTo("lb");
        assertThat(day.getFinishedAt()).isEqualTo(Instant.parse("2025-06-21T16:51:05.182Z"));
        assertThat(day.getLabel()).isNull();
        assertThat(day.getNotes()).isEmpty();
        assertThat(day.getExercises()).isNotEmpty();
        assertThat(day.getExercises().size()).isEqualTo(6);
        assertThat(day.getMuscleGroups()).isNotEmpty();
        assertThat(day.getMuscleGroups().size()).isEqualTo(4);
    }

    @Test
    public void dayWithExercisesAndMuscleGroupsDeserializationTest() throws IOException {
        ClassPathResource resource = new ClassPathResource("example/day.json");
        Day day = json.readObject(resource.getFile());

        assertThat(day.getId()).isEqualTo(19749541L);
        assertThat(day.getMesoId()).isEqualTo(790173L);
        
        assertThat(day.getExercises()).isNotEmpty();
        assertThat(day.getExercises().size()).isEqualTo(6);

        // DayExercise
        var firstExercise = day.getExercises().get(0);
        assertThat(firstExercise.getId()).isEqualTo(121219691L);
        assertThat(firstExercise.getDayId()).isEqualTo(19749541L);
        assertThat(firstExercise.getExerciseId()).isEqualTo(51L);
        assertThat(firstExercise.getPosition()).isEqualTo(0);
        assertThat(firstExercise.getJointPain()).isEqualTo(0);
        assertThat(firstExercise.getStatus()).isEqualTo("complete");
        
        // ExerciseSets
        assertThat(firstExercise.getSets()).isNotEmpty();
        assertThat(firstExercise.getSets().size()).isEqualTo(3);
        var firstSet = firstExercise.getSets().get(0);
        assertThat(firstSet.getId()).isEqualTo(157738019L);
        assertThat(firstSet.getDayExerciseId()).isEqualTo(121219691L);
        assertThat(firstSet.getPosition()).isEqualTo(0);
        assertThat(firstSet.getSetType()).isEqualTo("regular");
        assertThat(firstSet.getWeight()).isEqualTo(35.0f);
        assertThat(firstSet.getReps()).isEqualTo(9);
        
        // Muscle Groups
        assertThat(day.getMuscleGroups()).isNotEmpty();
        assertThat(day.getMuscleGroups().size()).isEqualTo(4);
        
        var firstMuscleGroup = day.getMuscleGroups().get(0);
        assertThat(firstMuscleGroup.getId()).isEqualTo(89102468L);
        assertThat(firstMuscleGroup.getDayId()).isEqualTo(19749541L);
        assertThat(firstMuscleGroup.getMuscleGroupId()).isEqualTo(2L);
        assertThat(firstMuscleGroup.getPump()).isEqualTo(1);
        assertThat(firstMuscleGroup.getSoreness()).isEqualTo(0);
        assertThat(firstMuscleGroup.getWorkload()).isEqualTo(1);
        assertThat(firstMuscleGroup.getRecommendedSets()).isEqualTo(5);
        assertThat(firstMuscleGroup.getStatus()).isEqualTo("complete");
    }

}
