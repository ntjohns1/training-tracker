package com.noslen.training_tracker.dto.day;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class DayPayloadJsonTest {
    @Autowired
    private JacksonTester<DayPayload> json;

    List<DayNotePayload> dayNotePayloads = new ArrayList<>();
    List<DayExercisePayload> dayExercisePayloads = new ArrayList<>();
    List<ExerciseSetPayload> exerciseSetPayloads = new ArrayList<>();
    List<DayMuscleGroupPayload> dayMuscleGroupPayloads = new ArrayList<>();
    DayPayload dayPayload;
    DayExercisePayload dayExercisePayload;
    ExerciseSetPayload exerciseSetPayload;
    DayMuscleGroupPayload dayMuscleGroupPayload;
    DayNotePayload dayNotePayload;
    ClassPathResource resource;

    @BeforeEach
    void setup() {
        exerciseSetPayload = new ExerciseSetPayload(
                157738019L,
                121219691L,
                0,
                "regular",
                35f,
                35f,
                30.75f,
                38.5f,
                9,
                9,
                null,
                "lb",
                Instant.parse("2025-06-16T17:43:59.697Z"),
                Instant.parse("2025-06-21T15:13:51.916Z"),
                "complete");
        dayExercisePayload = new DayExercisePayload(
                121219691L,
                19749541L,
                51L,
                0,
                0,
                Instant.parse("2025-06-12T00:44:33.064Z"),
                Instant.parse("2025-06-21T15:30:14.950Z"),
                121219664L,
                2L,
                exerciseSetPayloads,
                "complete");
        dayMuscleGroupPayload = new DayMuscleGroupPayload(
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
        exerciseSetPayloads.add(exerciseSetPayload);
        dayExercisePayloads.add(dayExercisePayload);
        dayMuscleGroupPayloads.add(dayMuscleGroupPayload);
        resource = new ClassPathResource("example/day.json");
    }

    @Test
    void testSerialize() throws IOException {
        dayPayload = new DayPayload(
                19749541L,
                790173L,
                1L,
                0L,
                Instant.parse("2025-06-12T00:44:33.064Z"),
                Instant.parse("2025-06-21T16:51:05.289Z"),
                161,
                Instant.parse("2025-06-12T00:48:59.703Z"),
                "lb",
                Instant.parse("2025-06-21T16:51:05.182Z"),
                null,
                dayNotePayloads,
                dayExercisePayloads,
                dayMuscleGroupPayloads,
                "complete");

        assertThat(json.write(dayPayload)).hasJsonPathValue("$", resource);
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.id", 19749541L);
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.mesoId", 790173L);
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.week", 1L);
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.position", 0L);
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.createdAt", "2025-06-12T00:44:33.064Z");
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.updatedAt", "2025-06-21T16:51:05.289Z");
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.bodyweight", 161);
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.bodyweightAt", "2025-06-12T00:48:59.703Z");
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.unit", "lb");
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.finishedAt", "2025-06-21T16:51:05.182Z");
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.notes", dayNotePayloads);
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.exercises[0].id", dayExercisePayloads.get(0).id());
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.exercises[0].dayId", dayExercisePayloads.get(0).dayId());
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.exercises[0].exerciseId",
                dayExercisePayloads.get(0).exerciseId());
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.exercises[0].position",
                dayExercisePayloads.get(0).position());
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.exercises[0].jointPain",
                dayExercisePayloads.get(0).jointPain());
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.exercises[0].createdAt",
                dayExercisePayloads.get(0).createdAt());
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.exercises[0].updatedAt",
                dayExercisePayloads.get(0).updatedAt());
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.exercises[0].sourceDayExerciseId",
                dayExercisePayloads.get(0).sourceDayExerciseId());
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.exercises[0].muscleGroupId",
                dayExercisePayloads.get(0).muscleGroupId());
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.exercises[0].sets", dayExercisePayloads.get(0).sets());
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.exercises[0].status",
                dayExercisePayloads.get(0).status());
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.muscleGroups[0]", dayMuscleGroupPayloads);
        assertThat(json.write(dayPayload)).hasJsonPathValue("$.status", "complete");
    }

    @Test
    void testDeserialize() throws IOException {
        dayPayload = json.readObject(resource.getFile());

        assertThat(dayPayload.id()).isEqualTo(19749541L);
        assertThat(dayPayload.mesoId()).isEqualTo(790173L);
        assertThat(dayPayload.week()).isEqualTo(1);
        assertThat(dayPayload.position()).isEqualTo(0);
        assertThat(dayPayload.createdAt()).isEqualTo(Instant.parse("2025-06-12T00:44:33.064Z"));
        assertThat(dayPayload.updatedAt()).isEqualTo(Instant.parse("2025-06-21T16:51:05.289Z"));
        assertThat(dayPayload.bodyweight()).isEqualTo(161);
        assertThat(dayPayload.bodyweightAt()).isEqualTo(Instant.parse("2025-06-12T00:48:59.703Z"));
        assertThat(dayPayload.unit()).isEqualTo("lb");
        assertThat(dayPayload.finishedAt()).isEqualTo(Instant.parse("2025-06-21T16:51:05.182Z"));
        assertThat(dayPayload.label()).isNull();
        assertThat(dayPayload.notes()).isEmpty();
        assertThat(dayPayload.exercises()).isNotEmpty();
        assertThat(dayPayload.exercises().get(0).id()).isEqualTo(121219691L);
        assertThat(dayPayload.exercises().get(0).dayId()).isEqualTo(19749541L);
        assertThat(dayPayload.exercises().get(0).exerciseId()).isEqualTo(51L);
        assertThat(dayPayload.muscleGroups()).isNotEmpty();
        assertThat(dayPayload.muscleGroups().get(0).id()).isEqualTo(89102468L);
        assertThat(dayPayload.muscleGroups().get(0).dayId()).isEqualTo(19749541L);
        assertThat(dayPayload.muscleGroups().get(0).muscleGroupId()).isEqualTo(2L);
        assertThat(dayPayload.status()).isEqualTo("complete");
    }
}
