package com.noslen.training_tracker.service.muscle_group;

import com.noslen.training_tracker.dto.muscle_group.ProgressionResponse;
import com.noslen.training_tracker.mapper.muscle_group.ProgressionMapper;
import com.noslen.training_tracker.model.muscle_group.MuscleGroup;
import com.noslen.training_tracker.model.muscle_group.Progression;
import com.noslen.training_tracker.repository.muscle_group.MuscleGroupRepo;
import com.noslen.training_tracker.repository.muscle_group.ProgressionRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ProgressionServiceImpl implements ProgressionService {

    private final ProgressionRepo repo;
    private final ProgressionMapper mapper;
    private final MuscleGroupRepo muscleGroupRepo;

    public ProgressionServiceImpl(ProgressionRepo progressionRepo, ProgressionMapper mapper, MuscleGroupRepo muscleGroupRepo) {
        this.repo = progressionRepo;
        this.mapper = mapper;
        this.muscleGroupRepo = muscleGroupRepo;
    }

    @Override
    public ProgressionResponse createProgression(ProgressionResponse progressionResponse) {
        if (progressionResponse == null) {
            throw new IllegalArgumentException("ProgressionResponse cannot be null");
        }

        // Convert payload to entity (with null MuscleGroup initially)
        Progression progression = mapper.toEntity(progressionResponse);

        // Fetch and set the MuscleGroup entity
        MuscleGroup muscleGroup = muscleGroupRepo.findById(progressionResponse.muscleGroupId())
                .orElseThrow(() -> new RuntimeException("MuscleGroup not found with id: " + progressionResponse.muscleGroupId()));
        
        // Create new entity with the resolved MuscleGroup
        progression = new Progression(
                progression.getId(),
                muscleGroup,
                progression.getMgProgressionType(),
                progression.getMesocycle()
        );

        // Save entity
        Progression savedEntity = repo.save(progression);

        // Convert back to payload and return
        return mapper.toPayload(savedEntity);
    }

    @Override
    public ProgressionResponse updateProgression(Long progressionId, ProgressionResponse progressionResponse) {
        if (progressionId == null) {
            throw new IllegalArgumentException("Progression ID cannot be null");
        }
        if (progressionResponse == null) {
            throw new IllegalArgumentException("ProgressionResponse cannot be null");
        }

        Optional<Progression> existingOptional = repo.findById(progressionId);
        if (existingOptional.isEmpty()) {
            throw new RuntimeException("Progression not found with id: " + progressionId);
        }

        Progression existing = existingOptional.get();
        
        // Update entity with payload data using mapper
        Progression updatedProgression = mapper.updateEntity(existing,
                                                             progressionResponse);
        
        // Handle MuscleGroup change if muscleGroupId is different
        if (progressionResponse.muscleGroupId() != 0 &&
            progressionResponse.muscleGroupId() != existing.getMuscleGroup().getId()) {
            
            MuscleGroup newMuscleGroup = muscleGroupRepo.findById(progressionResponse.muscleGroupId())
                    .orElseThrow(() -> new RuntimeException("MuscleGroup not found with id: " + progressionResponse.muscleGroupId()));
            
            // Create new entity with updated MuscleGroup
            updatedProgression = new Progression(
                    updatedProgression.getId(),
                    newMuscleGroup,
                    updatedProgression.getMgProgressionType(),
                    updatedProgression.getMesocycle()
            );
        }
        
        // Save updated entity
        Progression savedEntity = repo.save(updatedProgression);

        // Convert back to payload and return
        return mapper.toPayload(savedEntity);
    }

    @Override
    public void deleteProgression(Long progressionId) {
        if (progressionId == null) {
            throw new IllegalArgumentException("Progression ID cannot be null");
        }

        if (!repo.existsById(progressionId)) {
            throw new RuntimeException("Progression not found with id: " + progressionId);
        }

        repo.deleteById(progressionId);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgressionResponse getProgression(Long progressionId) {
        if (progressionId == null) {
            throw new IllegalArgumentException("Progression ID cannot be null");
        }

        Optional<Progression> progression = repo.findById(progressionId);
        if (progression.isEmpty()) {
            throw new RuntimeException("Progression not found with id: " + progressionId);
        }
        return mapper.toPayload(progression.get());
    }
}
