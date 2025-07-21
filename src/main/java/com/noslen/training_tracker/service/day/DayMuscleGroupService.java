package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.model.day.DayMuscleGroup;

import java.util.List;

public interface DayMuscleGroupService {

    DayMuscleGroup createDayMuscleGroup(Long dayId, Long muscleGroupId);
    void updateDayMuscleGroup(Long id, DayMuscleGroup dayMuscleGroup);
    void deleteDayMuscleGroup(Long id);
    DayMuscleGroup getDayMuscleGroup(Long id);
    List<DayMuscleGroup> getDayMuscleGroupsByDayId(Long dayId);
}
