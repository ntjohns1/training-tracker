package com.noslen.training_tracker.service.muscle_group;

import com.noslen.training_tracker.dto.muscle_group.MuscleGroupPayload;

import java.util.List;

public interface MuscleGroupService {

    List<MuscleGroupPayload> getAllMuscleGroups();

    MuscleGroupPayload getMuscleGroupById(Long id);

    MuscleGroupPayload createMuscleGroup(MuscleGroupPayload muscleGroupPayload);

    MuscleGroupPayload updateMuscleGroup(Long id, MuscleGroupPayload muscleGroupPayload);

    void deleteMuscleGroup(Long id);

}
