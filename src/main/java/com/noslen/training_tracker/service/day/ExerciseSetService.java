package com.noslen.training_tracker.service.day;

import java.util.List;

import com.noslen.training_tracker.dto.day.ExerciseSetPayload;

public interface ExerciseSetService {
    ExerciseSetPayload createExerciseSet(ExerciseSetPayload exerciseSetPayload);
    ExerciseSetPayload updateExerciseSet(Long id, ExerciseSetPayload exerciseSetPayload);
    ExerciseSetPayload getExerciseSet(Long id);
    void deleteExerciseSet(Long id);
    List<ExerciseSetPayload> getExerciseSetsByDayExerciseId(Long dayExerciseId);
}
