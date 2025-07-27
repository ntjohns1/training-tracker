package com.noslen.training_tracker.dto.day;

import com.noslen.training_tracker.dto.day.response.*;
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
public class DayResponseJsonTest {
    @Autowired
    private JacksonTester<DayResponse> json;

    List<DayNoteResponse> dayNoteResponses = new ArrayList<>();
    List<DayExerciseResponse> dayExerciseResponses = new ArrayList<>();
    List<ExerciseSetResponse> exerciseSetResponses = new ArrayList<>();
    List<DayMuscleGroupResponse> dayMuscleGroupResponses = new ArrayList<>();
    DayResponse dayResponse;
    DayExerciseResponse dayExerciseResponse;
    ExerciseSetResponse exerciseSetResponse;
    DayMuscleGroupResponse dayMuscleGroupResponse;
    DayNoteResponse dayNoteResponse;
    ClassPathResource resource;

    @BeforeEach
    void setup() {
        exerciseSetResponse = new ExerciseSetResponse(
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
        dayExerciseResponse = new DayExerciseResponse(
                121219691L,
                19749541L,
                51L,
                0,
                0,
                Instant.parse("2025-06-12T00:44:33.064Z"),
                Instant.parse("2025-06-21T15:30:14.950Z"),
                121219664L,
                2L,
                exerciseSetResponses,
                "complete");
        dayMuscleGroupResponse = new DayMuscleGroupResponse(
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
        exerciseSetResponses.add(exerciseSetResponse);
        dayExerciseResponses.add(dayExerciseResponse);
        dayMuscleGroupResponses.add(dayMuscleGroupResponse);
        resource = new ClassPathResource("example/day.json");
    }

    @Test
    void testSerialize() throws IOException {
        dayResponse = new DayResponse(
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
                dayNoteResponses,
                dayExerciseResponses,
                dayMuscleGroupResponses,
                "complete");

        assertThat(json.write(dayResponse)).hasJsonPathValue("$", resource);
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.id", 19749541L);
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.mesoId", 790173L);
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.week", 1L);
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.position", 0L);
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.createdAt", "2025-06-12T00:44:33.064Z");
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.updatedAt", "2025-06-21T16:51:05.289Z");
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.bodyweight", 161);
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.bodyweightAt", "2025-06-12T00:48:59.703Z");
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.unit", "lb");
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.finishedAt", "2025-06-21T16:51:05.182Z");
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.notes",
                                                             dayNoteResponses);
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.exercises[0].id", dayExerciseResponses.get(0).id());
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.exercises[0].dayId", dayExerciseResponses.get(0).dayId());
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.exercises[0].exerciseId",
                                                             dayExerciseResponses.get(0).exerciseId());
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.exercises[0].position",
                                                             dayExerciseResponses.get(0).position());
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.exercises[0].jointPain",
                                                             dayExerciseResponses.get(0).jointPain());
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.exercises[0].createdAt",
                                                             dayExerciseResponses.get(0).createdAt());
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.exercises[0].updatedAt",
                                                             dayExerciseResponses.get(0).updatedAt());
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.exercises[0].sourceDayExerciseId",
                                                             dayExerciseResponses.get(0).sourceDayExerciseId());
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.exercises[0].muscleGroupId",
                                                             dayExerciseResponses.get(0).muscleGroupId());
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.exercises[0].sets", dayExerciseResponses.get(0).sets());
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.exercises[0].status",
                                                             dayExerciseResponses.get(0).status());
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.muscleGroups[0]",
                                                             dayMuscleGroupResponses);
        assertThat(json.write(dayResponse)).hasJsonPathValue("$.status", "complete");
    }

    @Test
    void testDeserialize() throws IOException {
        dayResponse = json.readObject(resource.getFile());

        assertThat(dayResponse.id()).isEqualTo(19749541L);
        assertThat(dayResponse.mesoId()).isEqualTo(790173L);
        assertThat(dayResponse.week()).isEqualTo(1);
        assertThat(dayResponse.position()).isEqualTo(0);
        assertThat(dayResponse.createdAt()).isEqualTo(Instant.parse("2025-06-12T00:44:33.064Z"));
        assertThat(dayResponse.updatedAt()).isEqualTo(Instant.parse("2025-06-21T16:51:05.289Z"));
        assertThat(dayResponse.bodyweight()).isEqualTo(161);
        assertThat(dayResponse.bodyweightAt()).isEqualTo(Instant.parse("2025-06-12T00:48:59.703Z"));
        assertThat(dayResponse.unit()).isEqualTo("lb");
        assertThat(dayResponse.finishedAt()).isEqualTo(Instant.parse("2025-06-21T16:51:05.182Z"));
        assertThat(dayResponse.label()).isNull();
        assertThat(dayResponse.notes()).isEmpty();
        assertThat(dayResponse.exercises()).isNotEmpty();
        assertThat(dayResponse.exercises().get(0).id()).isEqualTo(121219691L);
        assertThat(dayResponse.exercises().get(0).dayId()).isEqualTo(19749541L);
        assertThat(dayResponse.exercises().get(0).exerciseId()).isEqualTo(51L);
        assertThat(dayResponse.muscleGroups()).isNotEmpty();
        assertThat(dayResponse.muscleGroups().get(0).id()).isEqualTo(89102468L);
        assertThat(dayResponse.muscleGroups().get(0).dayId()).isEqualTo(19749541L);
        assertThat(dayResponse.muscleGroups().get(0).muscleGroupId()).isEqualTo(2L);
        assertThat(dayResponse.status()).isEqualTo("complete");
    }
}
