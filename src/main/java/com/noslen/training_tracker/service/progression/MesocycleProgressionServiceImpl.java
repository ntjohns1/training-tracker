package com.noslen.training_tracker.service.progression;

import com.noslen.training_tracker.dto.day.request.CreateExerciseSetRequest;
import com.noslen.training_tracker.dto.day.request.FinishDayRequest;
import com.noslen.training_tracker.dto.day.request.UpdateDayMuscleGroupRequest;
import com.noslen.training_tracker.dto.day.response.DayExerciseResponse;
import com.noslen.training_tracker.dto.day.response.DayMuscleGroupResponse;
import com.noslen.training_tracker.dto.mesocycle.response.MesocycleResponse;
import com.noslen.training_tracker.enums.Status;
import com.noslen.training_tracker.service.day.DayExerciseService;
import com.noslen.training_tracker.service.day.DayMuscleGroupService;
import com.noslen.training_tracker.service.day.DayService;
import com.noslen.training_tracker.service.day.ExerciseSetService;
import com.noslen.training_tracker.service.mesocycle.MesocycleService;
import com.noslen.training_tracker.util.EnumConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MesocycleProgressionServiceImpl implements MesocycleProgressionService {

    private final DayService dayService;
    private final DayMuscleGroupService dayMuscleGroupService;
    private final DayExerciseService dayExerciseService;
    private final ExerciseSetService exerciseSetService;
    private final MesocycleService mesocycleService;

    public MesocycleProgressionServiceImpl(DayService dayService, DayMuscleGroupService dayMuscleGroupService, DayExerciseService dayExerciseService, ExerciseSetService exerciseSetService, MesocycleService mesocycleService) {
        this.dayService = dayService;
        this.dayMuscleGroupService = dayMuscleGroupService;
        this.dayExerciseService = dayExerciseService;
        this.exerciseSetService = exerciseSetService;
        this.mesocycleService = mesocycleService;
    }

    /**
     * For each muscle group in the completed day, computes the recommended set count from
     * the previous week's feedback, persists it (with status PROGRAMMED) onto next week's
     * matching DayMuscleGroup, and creates the corresponding ExerciseSets on next week's
     * exercises for that muscle group.
     *
     * @param finishDayRequest the completed day payload (feedback already persisted)
     */
    @Override
    public void processCompletedDayandProgramNext(FinishDayRequest finishDayRequest) {
        List<FinishDayRequest.DayMuscleGroupFinishRequest> muscleGroups = finishDayRequest.muscleGroups();
        if (muscleGroups == null || muscleGroups.isEmpty()) {
            return;
        }

        // Achieved weight/reps per exercise this week, used to compute next week's targets.
        Map<Long, AchievedPerformance> achievedByExercise = achievedPerformance(finishDayRequest);
        boolean nextWeekIsDeload = isNextWeekDeload(finishDayRequest);

        for (FinishDayRequest.DayMuscleGroupFinishRequest dmgFinish : muscleGroups) {
            Long currentDmgId = dmgFinish.id();

            DayMuscleGroupResponse currentDmg = dayMuscleGroupService.getDayMuscleGroup(currentDmgId);
            DayMuscleGroupResponse previousDmg =
                    dayMuscleGroupService.getMostRecentWithSameMuscleGroup(currentDmgId);

            // Deload weeks ignore feedback and hold the prior volume (their own light rules apply);
            // normal weeks compute recommended sets from the previous week's feedback.
            int recommendedSets = nextWeekIsDeload
                    ? deloadSetCount(previousDmg)
                    : computeRecommendedSets(currentDmg, previousDmg);

            // Locate next week's matching DayMuscleGroup (the week AFTER the one just completed)
            // and mark it programmed.
            DayMuscleGroupResponse nextDmg =
                    dayMuscleGroupService.getDayMuscleGroupForNextWeek(currentDmgId);

            // Idempotency: Kafka delivers at least once, so reprocessing an already-programmed
            // next week must be a no-op.
            if (EnumConverter.enumToString(Status.PROGRAMMED).equals(nextDmg.status())) {
                continue;
            }

            dayMuscleGroupService.updateDayMuscleGroup(nextDmg.id(),
                    new UpdateDayMuscleGroupRequest(nextDmg.id(), null, null, null, null, null,
                            null, recommendedSets, EnumConverter.enumToString(Status.PROGRAMMED)));

            distributeSetsAcrossExercises(nextDmg, dmgFinish.muscleGroupId(), recommendedSets,
                    achievedByExercise, nextWeekIsDeload);
        }
    }

    /**
     * Determines whether the week after the completed day is the deload week. Each digit of
     * {@code microRirs} is the RIR for one week and the final week is always the deload; so the
     * next week is a deload when it is the last week of the mesocycle.
     */
    private boolean isNextWeekDeload(FinishDayRequest finishDayRequest) {
        if (finishDayRequest.mesoId() == null) {
            return false;
        }
        MesocycleResponse meso = mesocycleService.getMesocycle(finishDayRequest.mesoId());
        if (meso == null || meso.microRirs() == null) {
            return false;
        }
        int totalWeeks = String.valueOf(meso.microRirs()).length();
        int currentWeek = finishDayRequest.week() == null ? 0 : finishDayRequest.week();
        return (currentWeek + 1) == (totalWeeks - 1);
    }

    /** Deload volume: hold the previous week's set count (deloads drop intensity, not volume). */
    private int deloadSetCount(DayMuscleGroupResponse previousDmg) {
        Integer previousSetCount =
                exerciseSetService.countExerciseSetsByMuscleGroupId(previousDmg.dayId(),
                                                                    previousDmg.muscleGroupId());
        return previousSetCount == null ? 0 : previousSetCount;
    }

    /** Last achieved weight/reps per exercise from the completed day. */
    private record AchievedPerformance(float weight, int reps) {}

    private Map<Long, AchievedPerformance> achievedPerformance(FinishDayRequest finishDayRequest) {
        Map<Long, AchievedPerformance> byExercise = new HashMap<>();
        if (finishDayRequest.exercises() == null) {
            return byExercise;
        }
        for (FinishDayRequest.DayExerciseFinishRequest exercise : finishDayRequest.exercises()) {
            if (exercise.exerciseId() == null || exercise.sets() == null) {
                continue;
            }
            // Use the last set with recorded weight and reps as the reference for progression.
            exercise.sets().stream()
                    .filter(s -> s.weight() != null && s.reps() != null)
                    .reduce((first, second) -> second)
                    .ifPresent(s -> byExercise.put(exercise.exerciseId(),
                            new AchievedPerformance(s.weight(), s.reps())));
        }
        return byExercise;
    }

    /**
     * @param dayMuscleGroupId the DayMuscleGroup on the completed day
     * @return recommended set count for next week
     */
    @Override
    public int calculateRecommendedSets(Long dayMuscleGroupId) {
        DayMuscleGroupResponse currentDmg = dayMuscleGroupService.getDayMuscleGroup(dayMuscleGroupId);
        DayMuscleGroupResponse previousDmg =
                dayMuscleGroupService.getMostRecentWithSameMuscleGroup(dayMuscleGroupId);
        return computeRecommendedSets(currentDmg, previousDmg);
    }

    /**
     * Pure feedback → recommended-sets computation. Soreness is sourced from the current
     * DayMuscleGroup; pump, workload, joint pain and the prior set count come from the
     * previous week.
     */
    private int computeRecommendedSets(DayMuscleGroupResponse currentDmg, DayMuscleGroupResponse previousDmg) {
        Integer maxJointPain =
                dayExerciseService.getDayExerciseMaxJointPain(previousDmg.dayId(),
                                                              previousDmg.muscleGroupId());
        Integer totalExerciseSets =
                exerciseSetService.countExerciseSetsByMuscleGroupId(previousDmg.dayId(),
                                                                    previousDmg.muscleGroupId());

        // Missing feedback/history degrades to neutral values rather than crashing:
        // no joint pain reported, no prior sets, no pump/workload feedback.
        return ProgressionCalculator.calculateRecommendedSets(
                totalExerciseSets != null ? totalExerciseSets : 0,
                maxJointPain != null ? maxJointPain : 0,
                previousDmg.pump() != null ? previousDmg.pump() : 0,
                currentDmg.soreness() != null ? currentDmg.soreness() : 0,
                previousDmg.workload() != null ? previousDmg.workload() : 0);
    }

    /**
     * Spreads {@code recommendedSets} across next week's exercises for the muscle group,
     * giving the remainder to the earliest positions, and creates one ExerciseSet per slot.
     */
    private void distributeSetsAcrossExercises(DayMuscleGroupResponse nextDmg, Long muscleGroupId,
                                               int recommendedSets,
                                               Map<Long, AchievedPerformance> achievedByExercise,
                                               boolean deload) {
        List<DayExerciseResponse> nextExercises =
                dayExerciseService.getDayExercisesByDayId(nextDmg.dayId())
                        .stream()
                        .filter(ex -> muscleGroupId.equals(ex.muscleGroupId()))
                        .sorted(Comparator.comparing(DayExerciseResponse::position))
                        .toList();

        if (nextExercises.isEmpty()) {
            return;
        }

        int[] distribution = ProgressionCalculator.distributeSets(recommendedSets, nextExercises.size());
        for (int i = 0; i < nextExercises.size(); i++) {
            DayExerciseResponse exercise = nextExercises.get(i);
            // Targets are derived from this week's achieved performance for the same exercise;
            // when there is no prior performance the set is created with null targets.
            WeightTargetCalculator.Targets targets = targetsFor(achievedByExercise.get(exercise.exerciseId()), deload);
            for (int position = 0; position < distribution[i]; position++) {
                exerciseSetService.createExerciseSet(new CreateExerciseSetRequest(
                        exercise.id(), position, null,
                        targets == null ? null : targets.weightTarget(),
                        targets == null ? null : targets.weightTargetMin(),
                        targets == null ? null : targets.weightTargetMax(),
                        targets == null ? null : targets.repsTarget(),
                        Instant.now()));
            }
        }
    }

    /**
     * Computes weight/rep targets from last week's achieved performance. Defaults to load
     * progression (+increment, reps held); rep-vs-weight progression per exercise is a
     * calibration refinement (see WeightTargetCalculator).
     */
    private WeightTargetCalculator.Targets targetsFor(AchievedPerformance achieved, boolean deload) {
        if (achieved == null) {
            return null;
        }
        if (deload) {
            return WeightTargetCalculator.deload(achieved.weight(), achieved.reps(),
                    WeightTargetCalculator.DEFAULT_INCREMENT);
        }
        return WeightTargetCalculator.progressWeight(achieved.weight(), achieved.reps(),
                WeightTargetCalculator.DEFAULT_INCREMENT);
    }
}
