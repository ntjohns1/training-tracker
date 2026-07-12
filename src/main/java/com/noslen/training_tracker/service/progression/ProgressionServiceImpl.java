package com.noslen.training_tracker.service.progression;

import com.noslen.training_tracker.dto.progression.response.ProgressionResponse;
import com.noslen.training_tracker.mapper.progression.ProgressionMapper;
import com.noslen.training_tracker.model.progression.MuscleGroup;
import com.noslen.training_tracker.model.progression.Progression;
import com.noslen.training_tracker.repository.progression.MuscleGroupRepo;
import com.noslen.training_tracker.repository.progression.ProgressionRepo;
import com.noslen.training_tracker.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service implementation for Progression operations.
 * Includes user data segregation through UserContext validation.
 */
@Service
@Transactional
public class ProgressionServiceImpl implements ProgressionService {

    private final ProgressionRepo repo;
    private final ProgressionMapper mapper;
    private final MuscleGroupRepo muscleGroupRepo;
    private final UserContext userContext;

    public ProgressionServiceImpl(ProgressionRepo progressionRepo, ProgressionMapper mapper, MuscleGroupRepo muscleGroupRepo, UserContext userContext) {
        this.repo = progressionRepo;
        this.mapper = mapper;
        this.muscleGroupRepo = muscleGroupRepo;
        this.userContext = userContext;
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

        // Note: User access validation for create operations should be handled 
        // at the controller level or through the mesocycle creation process
        // since progressions are typically created as part of mesocycle creation

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
        
        // Validate that the current user owns the mesocycle this progression belongs to
        userContext.validateUserAccess(existing.getMesocycle().getUserId());
        
        // Update entity with payload data using mapper
        Progression updatedProgression = mapper.updateEntity(existing, progressionResponse);
        
        // If muscle group ID changed, update the MuscleGroup entity
        if (!existing.getMuscleGroup().getId().equals(progressionResponse.muscleGroupId())) {
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

        Optional<Progression> progressionOptional = repo.findById(progressionId);
        if (progressionOptional.isEmpty()) {
            throw new RuntimeException("Progression not found with id: " + progressionId);
        }

        Progression progression = progressionOptional.get();
        
        // Validate that the current user owns the mesocycle this progression belongs to
        userContext.validateUserAccess(progression.getMesocycle().getUserId());

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

        // Validate that the current user owns the mesocycle this progression belongs to
        userContext.validateUserAccess(progression.get().getMesocycle().getUserId());

        return mapper.toPayload(progression.get());
    }
}
