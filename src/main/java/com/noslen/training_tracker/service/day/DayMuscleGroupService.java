package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.request.UpdateDayMuscleGroupRequest;
import com.noslen.training_tracker.dto.day.response.DayMuscleGroupResponse;

import java.util.List;

public interface DayMuscleGroupService {

    DayMuscleGroupResponse createDayMuscleGroup(Long dayId, Long muscleGroupId);

    DayMuscleGroupResponse updateDayMuscleGroup(Long id, UpdateDayMuscleGroupRequest dayMuscleGroupRequest);

    void deleteDayMuscleGroup(Long id);

    DayMuscleGroupResponse getDayMuscleGroup(Long id);

    List<DayMuscleGroupResponse> getDayMuscleGroupsByDayId(Long dayId);

    DayMuscleGroupResponse getMostRecentWithSameMuscleGroup(Long currentDmgId);

    DayMuscleGroupResponse getDayMuscleGroupAt(Integer week, Integer position,
            Long muscleGroupId);

    DayMuscleGroupResponse findNextDayMuscleGroup(Long currentDmgId);

    DayMuscleGroupResponse getNextDayMuscleGroupForNextWeek(Long currentDmgId);

    void updateRecommendedSetsForNext(Long currentDmgId);


}
