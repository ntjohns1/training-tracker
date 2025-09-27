package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.response.ExerciseSetResponse;
import com.noslen.training_tracker.mapper.day.ExerciseSetMapper;
import com.noslen.training_tracker.model.day.ExerciseSet;
import com.noslen.training_tracker.repository.day.ExerciseSetRepo;
import com.noslen.training_tracker.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for ExerciseSet operations.
 * Includes user data segregation through UserContext validation.
 */
@Service
public class ExerciseSetServiceImpl implements ExerciseSetService { 
    
    private final ExerciseSetRepo repo;
    private final ExerciseSetMapper mapper;
    private final UserContext userContext;

    public ExerciseSetServiceImpl(ExerciseSetRepo repo, ExerciseSetMapper mapper, UserContext userContext) {
        this.repo = repo;
        this.mapper = mapper;
        this.userContext = userContext;
    }

    @Override
    @Transactional
    public ExerciseSetResponse createExerciseSet(ExerciseSetResponse exerciseSetResponse) {
        if (exerciseSetResponse == null) {
            throw new IllegalArgumentException("ExerciseSetResponse cannot be null");
        }

        // Convert payload to entity
        ExerciseSet exerciseSet = mapper.toEntity(exerciseSetResponse);
        
        // Note: User access validation for create operations should be handled 
        // at the controller level since exercise sets are typically created as part of workout progression
        
        // Set creation timestamp
        exerciseSet.setCreatedAt(Instant.now());
        
        // Save entity
        ExerciseSet savedEntity = repo.save(exerciseSet);

        // Convert back to payload and return
        return mapper.toPayload(savedEntity);
    }

    @Override
    @Transactional
    public ExerciseSetResponse updateExerciseSet(Long id, ExerciseSetResponse exerciseSetResponse) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (exerciseSetResponse == null) {
            throw new IllegalArgumentException("ExerciseSetResponse cannot be null");
        }

        Optional<ExerciseSet> exerciseSetOptional = repo.findById(id);
        if (exerciseSetOptional.isEmpty()) {
            throw new RuntimeException("ExerciseSet not found with id: " + id);
        }

        ExerciseSet existingExerciseSet = exerciseSetOptional.get();
        
        // Validate that the current user owns the mesocycle this exercise set belongs to
        userContext.validateUserAccess(existingExerciseSet.getDayExercise().getDay().getMesocycle().getUserId());
        
        // Update entity with payload data
        mapper.updateEntity(existingExerciseSet, exerciseSetResponse);
        
        // Save updated entity
        ExerciseSet savedEntity = repo.save(existingExerciseSet);

        // Convert back to payload and return
        return mapper.toPayload(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciseSetResponse getExerciseSet(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<ExerciseSet> exerciseSetOptional = repo.findById(id);
        if (exerciseSetOptional.isEmpty()) {
            throw new RuntimeException("ExerciseSet not found with id: " + id);
        }

        ExerciseSet exerciseSet = exerciseSetOptional.get();
        
        // Validate that the current user owns the mesocycle this exercise set belongs to
        userContext.validateUserAccess(exerciseSet.getDayExercise().getDay().getMesocycle().getUserId());
        
        return mapper.toPayload(exerciseSet);
    }

    @Override
    @Transactional
    public void deleteExerciseSet(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<ExerciseSet> exerciseSetOptional = repo.findById(id);
        if (exerciseSetOptional.isEmpty()) {
            throw new RuntimeException("ExerciseSet not found with id: " + id);
        }

        ExerciseSet exerciseSet = exerciseSetOptional.get();
        
        // Validate that the current user owns the mesocycle this exercise set belongs to
        userContext.validateUserAccess(exerciseSet.getDayExercise().getDay().getMesocycle().getUserId());
        
        repo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseSetResponse> getExerciseSetsByDayExerciseId(Long dayExerciseId) {
        if (dayExerciseId == null) {
            throw new IllegalArgumentException("Day Exercise ID cannot be null");
        }

        List<ExerciseSet> exerciseSets = repo.findByDayExercise_Id(dayExerciseId);
        
        // Validate user access for the first exercise set (they should all belong to the same day exercise/mesocycle)
        if (!exerciseSets.isEmpty()) {
            userContext.validateUserAccess(exerciseSets.get(0).getDayExercise().getDay().getMesocycle().getUserId());
        }
        
        return mapper.toPayloadList(exerciseSets);
    }

    /**
     * @param dayId
     * @param muscleGroupId
     * @return
     */
    @Override
    public Integer countExerciseSetsByMuscleGroupId(Long dayId, Long muscleGroupId) {
        return repo.countByDayExercise_Day_IdAndDayExercise_MuscleGroup_Id(dayId, muscleGroupId);
    }
}
