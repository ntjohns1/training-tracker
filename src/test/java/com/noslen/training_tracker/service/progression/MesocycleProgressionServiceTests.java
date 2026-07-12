package com.noslen.training_tracker.service.progression;

import com.noslen.training_tracker.dto.day.request.FinishDayRequest;
import com.noslen.training_tracker.dto.day.request.UpdateDayMuscleGroupRequest;
import com.noslen.training_tracker.dto.day.response.DayExerciseResponse;
import com.noslen.training_tracker.dto.day.response.DayMuscleGroupResponse;
import com.noslen.training_tracker.dto.mesocycle.response.MesocycleResponse;
import com.noslen.training_tracker.service.mesocycle.MesocycleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import com.noslen.training_tracker.service.day.DayExerciseService;
import com.noslen.training_tracker.service.day.DayMuscleGroupService;
import com.noslen.training_tracker.service.day.DayService;
import com.noslen.training_tracker.service.day.ExerciseSetService;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class MesocycleProgressionServiceTests {

    @Mock
    private DayService dayService;

    @Mock
    private DayMuscleGroupService dayMuscleGroupService;

    @Mock
    private DayExerciseService dayExerciseService;

    @Mock
    private ExerciseSetService exerciseSetService;

    @Mock
    private MesocycleService mesocycleService;

    private MesocycleProgressionServiceImpl service;

    private AutoCloseable closeable;

    private static final Long MESO_ID = 200L;

    private static final Long CURRENT_DMG_ID = 1L;
    private static final Long PREVIOUS_DMG_ID = 2L;
    private static final Long NEXT_DMG_ID = 3L;
    private static final Long CURRENT_DAY_ID = 100L;
    private static final Long PREVIOUS_DAY_ID = 90L;
    private static final Long MUSCLE_GROUP_ID = 5L;

    @BeforeEach
    public void setUp() {
        closeable = openMocks(this);
        service = new MesocycleProgressionServiceImpl(dayService, dayMuscleGroupService,
                dayExerciseService, exerciseSetService, mesocycleService);
    }

    /** Builds a mesocycle response whose microRirs determines total weeks (one digit per week). */
    private MesocycleResponse meso(long microRirs) {
        return new MesocycleResponse(MESO_ID, "key", 1L, "test", null, "lb", null, null, microRirs,
                Instant.now(), Instant.now(), null, null, null, null, null, null, null, null, null,
                null, null, null, null, String.valueOf(microRirs).length(), List.of());
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    private DayMuscleGroupResponse dmg(Long id, Long dayId, Integer pump, Integer soreness, Integer workload) {
        return new DayMuscleGroupResponse(id, dayId, MUSCLE_GROUP_ID, pump, soreness, workload,
                Instant.now(), Instant.now(), null, "pendingFeedback");
    }

    private void stubHappyPath(DayMuscleGroupResponse current, DayMuscleGroupResponse previous,
                               Integer maxJointPain, Integer previousSetCount) {
        when(dayMuscleGroupService.getDayMuscleGroup(CURRENT_DMG_ID)).thenReturn(current);
        when(dayMuscleGroupService.getMostRecentWithSameMuscleGroup(CURRENT_DMG_ID)).thenReturn(previous);
        when(dayMuscleGroupService.getDayMuscleGroupForNextWeek(previous.id()))
                .thenReturn(dmg(NEXT_DMG_ID, 110L, null, null, null));
        when(dayExerciseService.getDayExerciseMaxJointPain(previous.dayId(), previous.muscleGroupId()))
                .thenReturn(maxJointPain);
        when(exerciseSetService.countExerciseSetsByMuscleGroupId(previous.dayId(), previous.muscleGroupId()))
                .thenReturn(previousSetCount);
        // 5-week scheme: from week 2 the next week (3) is not the last week -> not a deload
        when(mesocycleService.getMesocycle(MESO_ID)).thenReturn(meso(32108L));
    }

    @Nested
    @DisplayName("calculateRecommendedSets(dayMuscleGroupId)")
    class CalculateRecommendedSets {

        @Test
        @DisplayName("low-risk feedback increases previous set count by 1")
        void lowRiskIncreases() {
            DayMuscleGroupResponse current = dmg(CURRENT_DMG_ID, CURRENT_DAY_ID, null, 1, null);
            DayMuscleGroupResponse previous = dmg(PREVIOUS_DMG_ID, PREVIOUS_DAY_ID, 0, 0, 1);
            stubHappyPath(current, previous, 0, 3);

            int result = service.calculateRecommendedSets(CURRENT_DMG_ID);

            // previous=3, jointPain=0, pump=0, soreness=1 (current), workload=1 -> riskSum=2 -> +1
            assertEquals(4, result);
        }

        @Test
        @DisplayName("queries joint pain and set count against the PREVIOUS day, not the current day")
        void collaboratorArguments() {
            DayMuscleGroupResponse current = dmg(CURRENT_DMG_ID, CURRENT_DAY_ID, null, 1, null);
            DayMuscleGroupResponse previous = dmg(PREVIOUS_DMG_ID, PREVIOUS_DAY_ID, 0, 0, 1);
            stubHappyPath(current, previous, 0, 3);

            service.calculateRecommendedSets(CURRENT_DMG_ID);

            verify(dayExerciseService).getDayExerciseMaxJointPain(PREVIOUS_DAY_ID, MUSCLE_GROUP_ID);
            verify(exerciseSetService).countExerciseSetsByMuscleGroupId(PREVIOUS_DAY_ID, MUSCLE_GROUP_ID);
        }

        @Test
        @DisplayName("soreness is sourced from the CURRENT day muscle group")
        void sorenessFromCurrentDmg() {
            // current soreness=3, previous soreness=0, no joint pain -> soreness rule -> hold at 3
            DayMuscleGroupResponse current = dmg(CURRENT_DMG_ID, CURRENT_DAY_ID, null, 3, null);
            DayMuscleGroupResponse previous = dmg(PREVIOUS_DMG_ID, PREVIOUS_DAY_ID, 0, 0, 0);
            stubHappyPath(current, previous, 0, 3);

            int result = service.calculateRecommendedSets(CURRENT_DMG_ID);

            // if previous soreness (0) were used instead, the result would be 4 (+1)
            assertEquals(3, result);
        }

        @Test
        @DisplayName("pump and workload are sourced from the PREVIOUS day muscle group")
        void pumpAndWorkloadFromPreviousDmg() {
            // previous pump=0 -> increase; if current pump (2) were used, result would hold at 3
            DayMuscleGroupResponse current = dmg(CURRENT_DMG_ID, CURRENT_DAY_ID, 2, 0, 3);
            DayMuscleGroupResponse previous = dmg(PREVIOUS_DMG_ID, PREVIOUS_DAY_ID, 0, 0, 0);
            stubHappyPath(current, previous, 0, 3);

            int result = service.calculateRecommendedSets(CURRENT_DMG_ID);

            assertEquals(4, result);
        }

        @Test
        @DisplayName("max joint pain from previous day's exercises feeds the safety override")
        void jointPainSafetyOverride() {
            DayMuscleGroupResponse current = dmg(CURRENT_DMG_ID, CURRENT_DAY_ID, null, 0, null);
            DayMuscleGroupResponse previous = dmg(PREVIOUS_DMG_ID, PREVIOUS_DAY_ID, 0, 0, 0);
            stubHappyPath(current, previous, 3, 3);

            int result = service.calculateRecommendedSets(CURRENT_DMG_ID);

            // jointPain==3 -> previous-1
            assertEquals(2, result);
        }

        @Test
        @DisplayName("propagates RuntimeException when previous day muscle group is not found")
        void previousDmgNotFound() {
            when(dayMuscleGroupService.getDayMuscleGroup(CURRENT_DMG_ID))
                    .thenReturn(dmg(CURRENT_DMG_ID, CURRENT_DAY_ID, null, 1, null));
            when(dayMuscleGroupService.getMostRecentWithSameMuscleGroup(CURRENT_DMG_ID))
                    .thenThrow(new RuntimeException("Previous DayMuscleGroup not found for: " + CURRENT_DMG_ID));

            assertThrows(RuntimeException.class, () -> service.calculateRecommendedSets(CURRENT_DMG_ID));
        }
    }

    @Nested
    @DisplayName("processCompletedDayandProgramNext(finishDayRequest)")
    class ProcessCompletedDay {

        private FinishDayRequest finishDayRequest(List<FinishDayRequest.DayMuscleGroupFinishRequest> muscleGroups) {
            return new FinishDayRequest(CURRENT_DAY_ID, 200L, 2, 1, Instant.now(), Instant.now(),
                    180f, Instant.now(), "lb", Instant.now(), null, List.of(), List.of(),
                    muscleGroups, "pendingConfirmation");
        }

        private FinishDayRequest.DayMuscleGroupFinishRequest dmgFinish(Long id) {
            return new FinishDayRequest.DayMuscleGroupFinishRequest(id, CURRENT_DAY_ID, MUSCLE_GROUP_ID,
                    1, 1, 1, Instant.now(), Instant.now(), null, "pendingFeedback");
        }

        @Test
        @DisplayName("calculates recommended sets for each muscle group in the request")
        void calculatesForEachMuscleGroup() {
            DayMuscleGroupResponse current = dmg(CURRENT_DMG_ID, CURRENT_DAY_ID, 1, 1, 1);
            DayMuscleGroupResponse previous = dmg(PREVIOUS_DMG_ID, PREVIOUS_DAY_ID, 0, 0, 1);
            stubHappyPath(current, previous, 0, 3);

            service.processCompletedDayandProgramNext(finishDayRequest(List.of(dmgFinish(CURRENT_DMG_ID))));

            verify(dayMuscleGroupService).getDayMuscleGroup(CURRENT_DMG_ID);
            verify(dayMuscleGroupService).getMostRecentWithSameMuscleGroup(CURRENT_DMG_ID);
        }

        @Test
        @DisplayName("persists recommended sets to next week's DMG with status PROGRAMMED")
        void persistsRecommendedSetsToNextWeek() {
            DayMuscleGroupResponse current = dmg(CURRENT_DMG_ID, CURRENT_DAY_ID, 1, 1, 1);
            DayMuscleGroupResponse previous = dmg(PREVIOUS_DMG_ID, PREVIOUS_DAY_ID, 0, 0, 1);
            stubHappyPath(current, previous, 0, 3);

            service.processCompletedDayandProgramNext(finishDayRequest(List.of(dmgFinish(CURRENT_DMG_ID))));

            ArgumentCaptor<UpdateDayMuscleGroupRequest> captor =
                    ArgumentCaptor.forClass(UpdateDayMuscleGroupRequest.class);
            verify(dayMuscleGroupService).updateDayMuscleGroup(eq(NEXT_DMG_ID), captor.capture());

            UpdateDayMuscleGroupRequest update = captor.getValue();
            assertEquals(4, update.recommendedSets()); // previous=3, low risk -> +1
            assertEquals("programmed", update.status());
        }

        @Test
        @DisplayName("distributes recommended sets across exercises, earlier positions take the remainder")
        void distributesSetsAcrossExercises() {
            // e.g. 7 recommended sets across 3 exercises -> 3, 2, 2 by exercise position.
            // Expected collaborators: DayExerciseService to fetch next day's exercises for the
            // muscle group, ExerciseSetService.createExerciseSet(...) per set.
            DayMuscleGroupResponse current = dmg(CURRENT_DMG_ID, CURRENT_DAY_ID, 1, 1, 1);
            DayMuscleGroupResponse previous = dmg(PREVIOUS_DMG_ID, PREVIOUS_DAY_ID, 0, 0, 1);
            stubHappyPath(current, previous, 0, 6); // previous=6, low risk -> 7 recommended

            // next week's day (id 110 from stubHappyPath) has 3 exercises for this muscle group
            when(dayExerciseService.getDayExercisesByDayId(110L)).thenReturn(List.of(
                    nextExercise(11L, 0), nextExercise(12L, 1), nextExercise(13L, 2)));

            service.processCompletedDayandProgramNext(finishDayRequest(List.of(dmgFinish(CURRENT_DMG_ID))));

            // 7 sets total should be created across the next day's exercises for this muscle group
            verify(exerciseSetService, org.mockito.Mockito.times(7)).createExerciseSet(any());
        }

        private DayExerciseResponse nextExercise(Long id, Integer position) {
            return new DayExerciseResponse(id, 110L, id, position, null, Instant.now(), Instant.now(),
                    null, MUSCLE_GROUP_ID, List.of(), "empty");
        }

        @Test
        @DisplayName("skips normal progression when next week is a deload week")
        void skipsProgressionOnDeloadWeek() {
            // When the completed day is in the penultimate week, the next week is a deload:
            // recommended sets should NOT come from the feedback calculator (deload has its
            // own volume rules - see docs/progression_algorithm.md).
            DayMuscleGroupResponse current = dmg(CURRENT_DMG_ID, CURRENT_DAY_ID, 1, 1, 1);
            DayMuscleGroupResponse previous = dmg(PREVIOUS_DMG_ID, PREVIOUS_DAY_ID, 0, 0, 1);
            stubHappyPath(current, previous, 0, 3);
            // 4-week scheme (microRirs "2108"): from week 2 the next week (3) is the last -> deload
            when(mesocycleService.getMesocycle(MESO_ID)).thenReturn(meso(2108L));

            service.processCompletedDayandProgramNext(finishDayRequest(List.of(dmgFinish(CURRENT_DMG_ID))));

            verify(dayExerciseService, never()).getDayExerciseMaxJointPain(anyLong(), anyLong());
        }

        @Test
        @DisplayName("does nothing when the request has no muscle groups")
        void noMuscleGroups() {
            service.processCompletedDayandProgramNext(finishDayRequest(List.of()));

            verify(dayMuscleGroupService, never()).getDayMuscleGroup(anyLong());
            assertTrue(true);
        }
    }
}
