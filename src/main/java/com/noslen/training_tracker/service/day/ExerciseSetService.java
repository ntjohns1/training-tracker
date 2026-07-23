package com.noslen.training_tracker.service.day;

import java.util.List;

import com.noslen.training_tracker.dto.day.request.CreateExerciseSetRequest;
import com.noslen.training_tracker.dto.day.request.UpdateExerciseSetRequest;
import com.noslen.training_tracker.dto.day.response.ExerciseSetResponse;

public interface ExerciseSetService {
    ExerciseSetResponse createExerciseSet(CreateExerciseSetRequest exerciseSetRequest);
    ExerciseSetResponse updateExerciseSet(Long id, UpdateExerciseSetRequest exerciseSetRequest);
    ExerciseSetResponse getExerciseSet(Long id);
    void deleteExerciseSet(Long id);
    List<ExerciseSetResponse> getExerciseSetsByDayExerciseId(Long dayExerciseId);
    Integer countExerciseSetsByMuscleGroupId(Long dayId, Long muscleGroupId);
}
