package com.noslen.training_tracker.service.muscle_group;

import com.noslen.training_tracker.dto.muscle_group.MuscleGroupPayload;
import com.noslen.training_tracker.mapper.muscle_group.MuscleGroupMapper;
import com.noslen.training_tracker.model.muscle_group.MuscleGroup;
import com.noslen.training_tracker.repository.muscle_group.MuscleGroupRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

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
    public List<MuscleGroupPayload> getAllMuscleGroups() {
        List<MuscleGroup> muscleGroups = repo.findAll();
        return mapper.toPayloadList(muscleGroups);
    }

    @Override
    @Transactional(readOnly = true)
    public MuscleGroupPayload getMuscleGroupById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<MuscleGroup> muscleGroup = repo.findById(id);
        if (muscleGroup.isEmpty()) {
            throw new RuntimeException("MuscleGroup not found with id: " + id);
        }
        return mapper.toPayload(muscleGroup.get());
    }

    @Override
    public MuscleGroupPayload createMuscleGroup(MuscleGroupPayload muscleGroupPayload) {
        if (muscleGroupPayload == null) {
            throw new IllegalArgumentException("MuscleGroupPayload cannot be null");
        }

        // Convert payload to entity
        MuscleGroup muscleGroup = mapper.toEntity(muscleGroupPayload);
        
        // Set creation timestamp if not already set
        if (muscleGroup.getCreatedAt() == null) {
            muscleGroup.setCreatedAt(Instant.now());
        }
        if (muscleGroup.getUpdatedAt() == null) {
            muscleGroup.setUpdatedAt(Instant.now());
        }

        // Save entity
        MuscleGroup savedEntity = repo.save(muscleGroup);

        // Convert back to payload and return
        return mapper.toPayload(savedEntity);
    }

    @Override
    public MuscleGroupPayload updateMuscleGroup(Long id, MuscleGroupPayload muscleGroupPayload) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (muscleGroupPayload == null) {
            throw new IllegalArgumentException("MuscleGroupPayload cannot be null");
        }

        Optional<MuscleGroup> existingOptional = repo.findById(id);
        if (existingOptional.isEmpty()) {
            throw new RuntimeException("MuscleGroup not found with id: " + id);
        }

        MuscleGroup existing = existingOptional.get();
        
        // Update entity with payload data using mapper
        mapper.updateEntity(existing, muscleGroupPayload);
        
        // Ensure updated timestamp is set
        existing.setUpdatedAt(Instant.now());
        
        // Save updated entity
        MuscleGroup savedEntity = repo.save(existing);

        // Convert back to payload and return
        return mapper.toPayload(savedEntity);
    }

    @Override
    public void deleteMuscleGroup(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        if (!repo.existsById(id)) {
            throw new RuntimeException("MuscleGroup not found with id: " + id);
        }

        repo.deleteById(id);
    }
}
