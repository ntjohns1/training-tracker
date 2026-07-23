package com.noslen.training_tracker.service.progression;

import com.noslen.training_tracker.dto.progression.response.MuscleGroupResponse;
import com.noslen.training_tracker.mapper.progression.MuscleGroupMapper;
import com.noslen.training_tracker.model.progression.MuscleGroup;
import com.noslen.training_tracker.repository.progression.MuscleGroupRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Read access to muscle-group reference data. Muscle groups are seeded; writes are not part of
 * the MVP.
 */
@Service
@Transactional
public class MuscleGroupServiceImpl implements MuscleGroupService {

    private final MuscleGroupRepo repo;
    private final MuscleGroupMapper mapper;

    public MuscleGroupServiceImpl(MuscleGroupRepo muscleGroupRepo, MuscleGroupMapper mapper) {
        this.repo = muscleGroupRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MuscleGroupResponse> getAllMuscleGroups() {
        List<MuscleGroup> muscleGroups = repo.findAll();
        return mapper.toPayloadList(muscleGroups);
    }

    @Override
    @Transactional(readOnly = true)
    public MuscleGroupResponse getMuscleGroupById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<MuscleGroup> muscleGroup = repo.findById(id);
        if (muscleGroup.isEmpty()) {
            throw new RuntimeException("MuscleGroup not found with id: " + id);
        }
        return mapper.toPayload(muscleGroup.get());
    }
}
