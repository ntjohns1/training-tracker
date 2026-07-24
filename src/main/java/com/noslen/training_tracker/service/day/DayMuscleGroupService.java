package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.request.CreateDayMuscleGroupRequest;
import com.noslen.training_tracker.dto.day.request.UpdateDayMuscleGroupRequest;
import com.noslen.training_tracker.dto.day.response.DayMuscleGroupResponse;

import java.util.List;
import java.util.Optional;

public interface DayMuscleGroupService {

    DayMuscleGroupResponse createDayMuscleGroup(CreateDayMuscleGroupRequest dayMuscleGroupRequest);

    DayMuscleGroupResponse updateDayMuscleGroup(Long id, UpdateDayMuscleGroupRequest dayMuscleGroupRequest);

    void deleteDayMuscleGroup(Long id);

    DayMuscleGroupResponse getDayMuscleGroup(Long id);

    List<DayMuscleGroupResponse> getDayMuscleGroupsByDayId(Long dayId);

    DayMuscleGroupResponse getMostRecentWithSameMuscleGroup(Long currentDmgId);

    /**
     * Non-throwing variant: empty when this is the first occurrence of the muscle group
     * (i.e. week 1, which has no previous week to progress from).
     */
    Optional<DayMuscleGroupResponse> findMostRecentWithSameMuscleGroup(Long currentDmgId);

    /** Non-throwing variant: empty when there is no following week (i.e. the final week). */
    Optional<DayMuscleGroupResponse> findDayMuscleGroupForNextWeek(Long currentDmgId);

    DayMuscleGroupResponse getDayMuscleGroupAt(Integer week, Integer position,
            Long muscleGroupId);

    DayMuscleGroupResponse getNextDayMuscleGroup(Long currentDmgId);

    DayMuscleGroupResponse getDayMuscleGroupForNextWeek(Long currentDmgId);

}
