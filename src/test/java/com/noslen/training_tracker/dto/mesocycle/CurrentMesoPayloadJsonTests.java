package com.noslen.training_tracker.dto.mesocycle;

import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.io.ClassPathResource;

import com.noslen.training_tracker.dto.day.DayExercisePayload;
import com.noslen.training_tracker.dto.day.DayMuscleGroupPayload;
import com.noslen.training_tracker.dto.day.DayNotePayload;
import com.noslen.training_tracker.dto.day.DayPayload;
import com.noslen.training_tracker.dto.day.ExerciseSetPayload;
import com.noslen.training_tracker.dto.muscle_group.ProgressionPayload;
import com.noslen.training_tracker.util.MgProgressionType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CurrentMesoPayloadJsonTests {

        @Autowired
        private JacksonTester<CurrentMesoPayload> json;

        private List<Week> weeks;
        private Set<MesoNotePayload> notes;
        private Map<Long, ProgressionPayload> progressions;
        private CurrentMesoPayload currentMesoPayload;
        private ClassPathResource resource;

        @BeforeEach
        void setUp() {
                weeks = new ArrayList<>();
                notes = new HashSet<>();
                progressions = new HashMap<>();

                MesoNotePayload note = new MesoNotePayload(
                                13571L,
                                790173L,
                                1634147L,
                                Instant.parse("2025-07-05T19:06:19.128Z"),
                                Instant.parse("2025-07-05T19:06:19.128Z"),
                                "For P7: \nBack to cable Lateral Raises");
                notes.add(note);

                ProgressionPayload progression1 = new ProgressionPayload(
                                6159088L,
                                1L,
                                MgProgressionType.regular);
                progressions.put(1L, progression1);

                List<ExerciseSetPayload> sets = new ArrayList<>();

                List<DayExercisePayload> exercises = new ArrayList<>();
                DayExercisePayload exercise = new DayExercisePayload(
                                121219664L,
                                19749536L,
                                51L,
                                0,
                                0,
                                Instant.parse("2025-06-12T00:44:33.064Z"),
                                Instant.parse("2025-06-13T19:16:51.846Z"),
                                null,
                                2L,
                                sets,
                                "complete");
                exercises.add(exercise);

                List<DayNotePayload> dayNotes = new ArrayList<>();
                List<DayMuscleGroupPayload> muscleGroups = new ArrayList<>();

                DayPayload day = new DayPayload(
                                19749536L,
                                790173L,
                                0L,
                                0L,
                                Instant.parse("2025-06-12T00:44:33.064Z"),
                                Instant.parse("2025-06-13T20:19:45.724Z"),
                                161,
                                Instant.parse("2025-06-12T00:48:59.703Z"),
                                "lb",
                                Instant.parse("2025-06-13T20:19:45.693Z"),
                                null,
                                dayNotes,
                                exercises,
                                muscleGroups,
                                "complete");

                List<DayPayload> daysList = new ArrayList<>();
                daysList.add(day);

                Week week = new Week(daysList);
                weeks.add(week);
        }

        @Test
        void testSerialize() throws IOException {
                currentMesoPayload = new CurrentMesoPayload(
                                790173L,
                                "wzzidovd6137",
                                1518614L,
                                "2025 P6",
                                5,
                                "lb",
                                16909L,
                                null,
                                32108L,
                                Instant.parse("2025-06-12T00:44:33.064Z"),
                                Instant.parse("2025-07-05T16:02:18.167Z"),
                                null,
                                null,
                                Instant.parse("2025-06-19T20:16:44.012Z"),
                                Instant.parse("2025-06-13T20:19:45.802Z"),
                                Instant.parse("2025-06-13T19:16:49.001Z"),
                                Instant.parse("2025-06-13T19:16:44.810Z"),
                                Instant.parse("2025-07-02T13:49:49.037Z"),
                                Instant.parse("2025-07-05T16:00:02.611Z"),
                                null,
                                Instant.parse("2025-07-05T16:02:18.077Z"),
                                Instant.parse("2025-07-05T16:02:18.077Z"),
                                null,
                                null,
                                weeks,
                                notes,
                                "ready",
                                "Thor Workout",
                                progressions);

                resource = new ClassPathResource("example/current_meso.json");

                assertThat(json.write(currentMesoPayload)).extractingJsonPathNumberValue("$.id").isEqualTo(790173);
                assertThat(json.write(currentMesoPayload)).extractingJsonPathStringValue("$.key")
                                .isEqualTo("wzzidovd6137");
                assertThat(json.write(currentMesoPayload)).extractingJsonPathNumberValue("$.userId").isEqualTo(1518614);
                assertThat(json.write(currentMesoPayload)).extractingJsonPathStringValue("$.name").isEqualTo("2025 P6");

                assertThat(json.write(currentMesoPayload)).hasJsonPathArrayValue("$.weeks");

                assertThat(json.write(currentMesoPayload)).hasJsonPathArrayValue("$.weeks[0].days");
                assertThat(json.write(currentMesoPayload)).extractingJsonPathNumberValue("$.weeks[0].days[0].id")
                                .isEqualTo(19749536);

                assertThat(json.write(currentMesoPayload)).hasJsonPathArrayValue("$.weeks[0].days[0].exercises");
                assertThat(json.write(currentMesoPayload))
                                .extractingJsonPathNumberValue("$.weeks[0].days[0].exercises[0].id")
                                .isEqualTo(121219664);

                assertThat(json.write(currentMesoPayload)).hasJsonPathArrayValue("$.notes");
                assertThat(json.write(currentMesoPayload)).extractingJsonPathNumberValue("$.notes[0].id")
                                .isEqualTo(13571);

                assertThat(json.write(currentMesoPayload)).extractingJsonPathStringValue("$.status").isEqualTo("ready");
                assertThat(json.write(currentMesoPayload)).extractingJsonPathStringValue("$.generatedFrom")
                                .isEqualTo("Thor Workout");

                assertThat(json.write(currentMesoPayload)).hasJsonPath("$.progressions");
                assertThat(json.write(currentMesoPayload)).extractingJsonPathNumberValue("$.progressions['1'].id")
                                .isEqualTo(6159088);
        }

        @Test
        void testDeserialize() throws IOException {
                resource = new ClassPathResource("example/current_meso.json");
                CurrentMesoPayload mesocycle = json.readObject(resource.getFile());

                assertThat(mesocycle.id()).isEqualTo(790173L);
                assertThat(mesocycle.key()).isEqualTo("wzzidovd6137");
                assertThat(mesocycle.userId()).isEqualTo(1518614L);
                assertThat(mesocycle.name()).isEqualTo("2025 P6");
                assertThat(mesocycle.days()).isEqualTo(5);

                assertThat(mesocycle.weeks()).isNotEmpty();
                assertThat(mesocycle.notes()).isNotEmpty();
                assertThat(mesocycle.progressions()).isNotEmpty();
                assertThat(mesocycle.progressions().get(1L)).isNotNull();
                assertThat(mesocycle.progressions().get(1L).id()).isEqualTo(6159088L);

                Week firstWeek = mesocycle.weeks().get(0);
                assertThat(firstWeek.days()).isNotEmpty();

                DayPayload firstDay = firstWeek.days().get(0);
                assertThat(firstDay.id()).isEqualTo(19749536L);
                assertThat(firstDay.exercises()).isNotEmpty();

                assertThat(mesocycle.status()).isEqualTo("ready");
                assertThat(mesocycle.generatedFrom()).isEqualTo("Thor Workout");
        }
}