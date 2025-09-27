package com.noslen.training_tracker.service.day;

import java.util.List;

import com.noslen.training_tracker.dto.day.response.ExerciseSetResponse;

public interface ExerciseSetService {
    ExerciseSetResponse createExerciseSet(ExerciseSetResponse exerciseSetResponse);
    ExerciseSetResponse updateExerciseSet(Long id, ExerciseSetResponse exerciseSetResponse);
    ExerciseSetResponse getExerciseSet(Long id);
    void deleteExerciseSet(Long id);
    List<ExerciseSetResponse> getExerciseSetsByDayExerciseId(Long dayExerciseId);
    Integer countExerciseSetsByMuscleGroupId(Long dayId, Long muscleGroupId);
}
