package com.noslen.training_tracker.service.progression;

import com.noslen.training_tracker.dto.day.request.FinishDayRequest;
import com.noslen.training_tracker.dto.day.response.DayMuscleGroupResponse;
import com.noslen.training_tracker.dto.day.response.DayResponse;
import com.noslen.training_tracker.service.day.DayExerciseService;
import com.noslen.training_tracker.service.day.DayMuscleGroupService;
import com.noslen.training_tracker.service.day.DayService;
import com.noslen.training_tracker.service.day.ExerciseSetService;

public class MesocycleProgressionServiceImpl implements MesocycleProgressionService {
    //    private final DayMuscleGroupRepository repo;
    private final DayService dayService;
    private final DayMuscleGroupService dayMuscleGroupService;
    private final DayExerciseService dayExerciseService;
    private final ExerciseSetService exerciseSetService;

    public MesocycleProgressionServiceImpl(DayService dayService, DayMuscleGroupService dayMuscleGroupService, DayExerciseService dayExerciseService, ExerciseSetService exerciseSetService) {
        this.dayService = dayService;
        this.dayMuscleGroupService = dayMuscleGroupService;
        this.dayExerciseService = dayExerciseService;
        this.exerciseSetService = exerciseSetService;
    }

    /**
     * @param finishDayRequest
     */
    @Override
    public void processCompletedDayandProgramNext(FinishDayRequest finishDayRequest) {
        // calculate recommended sets for next day
        for (FinishDayRequest.DayMuscleGroupFinishRequest dmgFinishRequest :
                finishDayRequest.muscleGroups()) {
//            dayMuscleGroupService.updateRecommendedSetsForNext(dmgFinishRequest.id());
            // create ExerciseSets for next day based on recommended sets
//            DayResponse nextDay = dayService.getNextDayWithSameMuscleGroup(finishDayRequest.id(),
//                                                                           dmgFinishRequest.muscleGroupId());
            // get recommended sets for next day
            Integer recommendedSets = calculateRecommendedSets(dmgFinishRequest.id());
            // get count of exercises for next day for next day muscle group
//            Integer totalExerciseSets =
//                    dayExerciseService.countDayExercisesByDayIdAndMuscleGroupId(nextDay.id(),
//                                                                                dmgFinishRequest.muscleGroupId());

            // TODO: divide recommended sets by total exercises - figure out how to distribute sets

            // save ExerciseSet

        }

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
                dayMuscleGroupService.getDayMuscleGroupForNextWeek(previousDmg.id());

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
