package com.noslen.training_tracker.service.progression;

import com.noslen.training_tracker.dto.day.request.FinishDayRequest;
import com.noslen.training_tracker.dto.day.response.DayMuscleGroupResponse;
import com.noslen.training_tracker.model.day.DayMuscleGroup;
import com.noslen.training_tracker.service.day.DayExerciseService;
import com.noslen.training_tracker.service.day.DayMuscleGroupService;
import com.noslen.training_tracker.service.day.ExerciseSetService;

import java.util.Optional;

public class MesocycleProgressionServiceImpl implements MesocycleProgressionService {
    //    private final DayMuscleGroupRepository repo;
    private final DayMuscleGroupService dayMuscleGroupService;
    private final DayExerciseService dayExerciseService;
    private final ExerciseSetService exerciseSetService;

    public MesocycleProgressionServiceImpl(DayMuscleGroupService dayMuscleGroupService, DayExerciseService dayExerciseService, ExerciseSetService exerciseSetService) {
        this.dayMuscleGroupService = dayMuscleGroupService;
        this.dayExerciseService = dayExerciseService;
        this.exerciseSetService = exerciseSetService;
    }

    /**
     * @param finishDayRequest
     */
    @Override
    public void processCompletedDayandProgramNext(FinishDayRequest finishDayRequest) {

    }

    /**
     * @param dayMuscleGroupId
     * @return
     */
    @Override
    public int calculateRecommendedSets(Long dayMuscleGroupId) {
        //        TODO: check if next week is deload week
        DayMuscleGroupResponse currentDmg = dayMuscleGroupService.getDayMuscleGroup(dayMuscleGroupId);

        Optional<DayMuscleGroup> previousDmgOpt = repo.findMostRecentWithSameMuscleGroup(dayMuscleGroupId);
        if (previousDmgOpt.isEmpty()) {
            throw new RuntimeException("Previous DayMuscleGroup not found for: " + dayMuscleGroupId);
        }
        DayMuscleGroup previousDmg = previousDmgOpt.get();
        DayMuscleGroup currentDmg = currentDmgOpt.get();

        Optional<DayMuscleGroup> nextDmgOpt =
                repo.findDayMuscleGroupAt(previousDmg.getDay()
                                                  .getWeek() + 1,
                                          previousDmg.getDay()
                                                  .getPosition(),
                                          previousDmg.getMuscleGroupId());
        if (nextDmgOpt.isEmpty()) {
            throw new RuntimeException("Next DayMuscleGroup not found for: " + dayMuscleGroupId);
        }
        DayMuscleGroup nextDmg = nextDmgOpt.get();
        Integer maxJointPain =
                dayExerciseService.getDayExerciseMaxJointPain(previousDmg.getDayId(),
                                                              previousDmg.getMuscleGroupId());
        Integer totalExerciseSets =
                exerciseSetService.countExerciseSetsByMuscleGroupId(previousDmg.getDayId(),
                                                                    previousDmg.getMuscleGroupId());

        return ProgressionCalculator.calculateRecommendedSets(totalExerciseSets,
                                                              maxJointPain,
                                                              previousDmg.getPump(),
                                                              currentDmg.getSoreness(),
                                                              previousDmg.getWorkload());
//        nextDmg.setStatus(Status.PROGRAMMED);
//        nextDmg.setUpdatedAt(Instant.now());
//        repo.save(nextDmg);
    }
}
