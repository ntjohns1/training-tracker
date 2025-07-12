package com.noslen.training_tracker.service.muscle_group;

import com.noslen.training_tracker.model.muscle_group.MuscleGroup;

import java.util.List;

public interface MuscleGroupService {

    List<MuscleGroup> getAllMuscleGroups();

    MuscleGroup getMuscleGroupById(Long id);

}
