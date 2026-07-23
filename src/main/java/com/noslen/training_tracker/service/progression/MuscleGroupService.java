package com.noslen.training_tracker.service.progression;

import com.noslen.training_tracker.dto.progression.response.MuscleGroupResponse;

import java.util.List;

public interface MuscleGroupService {

    List<MuscleGroupResponse> getAllMuscleGroups();

    MuscleGroupResponse getMuscleGroupById(Long id);

}
