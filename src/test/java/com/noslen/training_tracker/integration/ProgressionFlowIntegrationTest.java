package com.noslen.training_tracker.integration;

import com.noslen.training_tracker.dto.day.request.CreateExerciseSetRequest;
import com.noslen.training_tracker.dto.day.request.FinishDayRequest;
import com.noslen.training_tracker.dto.day.request.UpdateDayExerciseRequest;
import com.noslen.training_tracker.dto.day.request.UpdateDayMuscleGroupRequest;
import com.noslen.training_tracker.dto.mesocycle.request.CreateMesocycleRequest;
import com.noslen.training_tracker.dto.mesocycle.response.CurrentMesoResponse;
import com.noslen.training_tracker.dto.day.response.DayMuscleGroupResponse;
import com.noslen.training_tracker.dto.day.response.DayResponse;
import com.noslen.training_tracker.dto.day.response.ExerciseSetResponse;
import com.noslen.training_tracker.service.day.DayExerciseService;
import com.noslen.training_tracker.service.day.DayMuscleGroupService;
import com.noslen.training_tracker.service.day.ExerciseSetService;
import com.noslen.training_tracker.service.mesocycle.MesocycleService;
import com.noslen.training_tracker.service.progression.MesocycleProgressionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Golden-master-lite: exercises the real progression engine end-to-end against persistence
 * (H2), synchronously (calling the progression service directly rather than via Kafka). It
 * verifies the mid-mesocycle transition the captured data demonstrates: completing a week with
 * low-risk feedback adds a set to the next week's matching muscle group and creates the sets.
 *
 * <p>The full 16-transition Kafka replay of {@code full_meso} is deferred to a Testcontainers
 * (Postgres + Kafka) run in a Docker environment — see the plan.</p>
 */
@SpringBootTest
@ActiveProfiles("dev")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:progressiontest;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "app.kafka.consumer.auto-startup=false"
})
class ProgressionFlowIntegrationTest {

    @Autowired private MesocycleService mesocycleService;
    @Autowired private DayMuscleGroupService dayMuscleGroupService;
    @Autowired private DayExerciseService dayExerciseService;
    @Autowired private ExerciseSetService exerciseSetService;
    @Autowired private MesocycleProgressionService progressionService;

    @Test
    void completingAWeekProgramsTheNextWeeksMuscleGroup() {
        Instant now = Instant.now();

        // 1. Create a 4-week meso: 2 day patterns, one exercise each (exercises 1 and 2 from the seeded catalog).
        CreateMesocycleRequest request = new CreateMesocycleRequest(
                "Progression Test", 4,
                List.of(
                        new CreateMesocycleRequest.DayRequest("Push",
                                List.of(new CreateMesocycleRequest.DayExerciseRequest(1L))),
                        new CreateMesocycleRequest.DayRequest("Pull",
                                List.of(new CreateMesocycleRequest.DayExerciseRequest(2L)))
                ),
                "lb", null, null, null);
        Long mesoId = mesocycleService.createMesocycle(request).id();

        CurrentMesoResponse meso = mesocycleService.getCurrentMeso(mesoId);
        // day position 1 ("Push") across weeks 1, 2, 3
        DayResponse week1Day = meso.weeks().get(0).days().get(0);
        DayResponse week2Day = meso.weeks().get(1).days().get(0);
        DayResponse week3Day = meso.weeks().get(2).days().get(0);

        DayMuscleGroupResponse week1Dmg = week1Day.muscleGroups().get(0);
        DayMuscleGroupResponse week2Dmg = week2Day.muscleGroups().get(0);
        DayMuscleGroupResponse week3Dmg = week3Day.muscleGroups().get(0);
        Long muscleGroupId = week1Dmg.muscleGroupId();
        Long week1ExerciseId = week1Day.exercises().get(0).id();

        // 2. Week 1 (the "previous" week) was performed: 2 sets, low-risk feedback, marked complete.
        exerciseSetService.createExerciseSet(new CreateExerciseSetRequest(week1ExerciseId, 0, "regular", null, null, null, null, now));
        exerciseSetService.createExerciseSet(new CreateExerciseSetRequest(week1ExerciseId, 1, "regular", null, null, null, null, now));
        dayExerciseService.updateDayExercise(week1ExerciseId,
                new UpdateDayExerciseRequest(week1ExerciseId, null, null, null, 0, now, null, null, "complete"));
        dayMuscleGroupService.updateDayMuscleGroup(week1Dmg.id(),
                new UpdateDayMuscleGroupRequest(week1Dmg.id(), null, null, 0, 0, 1, now, null, "complete"));

        // 3. Week 2 (the "current" week) reports low soreness.
        dayMuscleGroupService.updateDayMuscleGroup(week2Dmg.id(),
                new UpdateDayMuscleGroupRequest(week2Dmg.id(), null, null, null, 1, null, now, null, null));

        // 4. Complete the second training week (0-indexed week 1) -> programs week index 2 (not
        // the deload; a 4-week meso's deload is week index 3).
        FinishDayRequest finish = new FinishDayRequest(
                week2Day.id(), mesoId, 1, 1, now, now, null, null, "lb", now, null,
                List.of(), List.of(),
                List.of(new FinishDayRequest.DayMuscleGroupFinishRequest(
                        week2Dmg.id(), week2Day.id(), muscleGroupId, 0, 1, 1, now, now, null, "complete")),
                "pendingConfirmation");
        progressionService.processCompletedDayandProgramNext(finish);

        // 5. Week 3's matching DMG is programmed; low-risk feedback bumps the prior 2 sets to 3.
        DayMuscleGroupResponse programmed = dayMuscleGroupService.getDayMuscleGroup(week3Dmg.id());
        assertEquals("programmed", programmed.status());
        assertEquals(3, programmed.recommendedSets());

        // 6. Week 3's exercise received the 3 distributed sets.
        Long week3ExerciseId = week3Day.exercises().get(0).id();
        List<ExerciseSetResponse> week3Sets =
                exerciseSetService.getExerciseSetsByDayExerciseId(week3ExerciseId);
        assertEquals(3, week3Sets.size());
        assertTrue(week3Sets.stream().allMatch(s -> "programmed".equals(programmed.status())));
    }
}
