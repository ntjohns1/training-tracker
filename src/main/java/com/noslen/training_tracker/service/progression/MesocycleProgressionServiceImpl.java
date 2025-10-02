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

        DayMuscleGroupResponse previousDmg =
                dayMuscleGroupService.getMostRecentWithSameMuscleGroup(dayMuscleGroupId);


        DayMuscleGroupResponse nextDmg =
                dayMuscleGroupService.getNextDayMuscleGroupForNextWeek(previousDmg.id());

        Integer maxJointPain =
                dayExerciseService.getDayExerciseMaxJointPain(previousDmg.dayId(),
                                                              previousDmg.muscleGroupId());
        Integer totalExerciseSets =
                exerciseSetService.countExerciseSetsByMuscleGroupId(previousDmg.dayId(),
                                                                    previousDmg.muscleGroupId());

        return ProgressionCalculator.calculateRecommendedSets(totalExerciseSets,
                                                              maxJointPain,
                                                              previousDmg.pump(),
                                                              currentDmg.soreness(),
                                                              previousDmg.workload());
//        nextDmg.setStatus(Status.PROGRAMMED);
//        nextDmg.setUpdatedAt(Instant.now());
//        repo.save(nextDmg);
    }
}
