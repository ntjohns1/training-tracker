package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.DayMuscleGroupResponse;

import java.util.List;

public interface DayMuscleGroupService {

    DayMuscleGroupResponse createDayMuscleGroup(Long dayId, Long muscleGroupId);
    DayMuscleGroupResponse updateDayMuscleGroup(Long id, DayMuscleGroupResponse dayMuscleGroupResponse);
    void deleteDayMuscleGroup(Long id);
    DayMuscleGroupResponse getDayMuscleGroup(Long id);
    List<DayMuscleGroupResponse> getDayMuscleGroupsByDayId(Long dayId);
}
