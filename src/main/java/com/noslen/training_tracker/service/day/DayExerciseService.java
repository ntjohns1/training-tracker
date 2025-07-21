package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.DayExercisePayload;

import java.util.List;

public interface DayExerciseService {
    DayExercisePayload createDayExercise(DayExercisePayload dayExercisePayload);
    DayExercisePayload updateDayExercise(Long id, DayExercisePayload dayExercisePayload);
    void deleteDayExercise(Long id);
    DayExercisePayload getDayExercise(Long id);
    DayExercisePayload getDayExercise(Long dayId, Long exerciseId);
    List<DayExercisePayload> getDayExercisesByDayId(Long dayId);
}
