package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.response.DayExerciseResponse;

import java.util.List;

public interface DayExerciseService {
    DayExerciseResponse createDayExercise(DayExerciseResponse dayExerciseResponse);
    DayExerciseResponse updateDayExercise(Long id, DayExerciseResponse dayExerciseResponse);
    void deleteDayExercise(Long id);
    DayExerciseResponse getDayExercise(Long id);
    DayExerciseResponse getDayExercise(Long dayId, Long exerciseId);
    List<DayExerciseResponse> getDayExercisesByDayId(Long dayId);
}
