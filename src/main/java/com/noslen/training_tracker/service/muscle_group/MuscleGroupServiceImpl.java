package com.noslen.training_tracker.service.muscle_group;

import com.noslen.training_tracker.model.muscle_group.MuscleGroup;
import com.noslen.training_tracker.repository.muscle_group.MuscleGroupRepo;

import java.util.List;
import java.util.Optional;

public class MuscleGroupServiceImpl implements MuscleGroupService {

    private final MuscleGroupRepo repo;

    public MuscleGroupServiceImpl(MuscleGroupRepo muscleGroupRepo) {
        this.repo = muscleGroupRepo;
    }

    /**
     * @return
     */
    @Override
    public List<MuscleGroup> getAllMuscleGroups() {
        return this.repo.findAll();
    }

    /**
     * @param id
     * @return
     */
    @Override
    public MuscleGroup getMuscleGroupById(Long id) {
        Optional<MuscleGroup> mg = this.repo.findById(id);
        return mg.orElse(null);
    }

}
