package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.request.CreateDayExerciseRequest;
import com.noslen.training_tracker.dto.day.request.UpdateDayExerciseRequest;
import com.noslen.training_tracker.dto.day.response.DayExerciseResponse;

import java.util.List;

public interface DayExerciseService {
    DayExerciseResponse createDayExercise(CreateDayExerciseRequest dayExerciseRequest);
    DayExerciseResponse updateDayExercise(Long id, UpdateDayExerciseRequest dayExerciseRequest);
    void deleteDayExercise(Long id);
    DayExerciseResponse getDayExercise(Long id);
    DayExerciseResponse getDayExercise(Long dayId, Long exerciseId);
    List<DayExerciseResponse> getDayExercisesByDayId(Long dayId);
    Integer getDayExerciseMaxJointPain(Long dayId, Long muscleGroupId);
    Integer countDayExercisesByDayIdAndMuscleGroupId(Long dayId, Long muscleGroupId);
}
