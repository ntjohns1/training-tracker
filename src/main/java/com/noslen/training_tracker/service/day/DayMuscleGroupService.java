package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.DayMuscleGroupPayload;

import java.util.List;

public interface DayMuscleGroupService {

    DayMuscleGroupPayload createDayMuscleGroup(Long dayId, Long muscleGroupId);
    DayMuscleGroupPayload updateDayMuscleGroup(Long id, DayMuscleGroupPayload dayMuscleGroupPayload);
    void deleteDayMuscleGroup(Long id);
    DayMuscleGroupPayload getDayMuscleGroup(Long id);
    List<DayMuscleGroupPayload> getDayMuscleGroupsByDayId(Long dayId);
}
