package com.noslen.training_tracker.service.day;

import com.noslen.training_tracker.dto.day.request.CreateExerciseSetRequest;
import com.noslen.training_tracker.dto.day.request.UpdateExerciseSetRequest;
import com.noslen.training_tracker.dto.day.response.ExerciseSetResponse;
import com.noslen.training_tracker.enums.SetType;
import com.noslen.training_tracker.mapper.day.ExerciseSetMapper;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.model.day.ExerciseSet;
import com.noslen.training_tracker.repository.day.ExerciseSetRepo;
import com.noslen.training_tracker.security.UserContext;
import com.noslen.training_tracker.util.EnumConverter;
import jakarta.persistence.EntityManager;
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

    private final EntityManager entityManager;
    private final ExerciseSetRepo repo;
    private final ExerciseSetMapper mapper;
    private final UserContext userContext;

    public ExerciseSetServiceImpl(EntityManager entityManager, ExerciseSetRepo repo, ExerciseSetMapper mapper, UserContext userContext) {
        this.entityManager = entityManager;
        this.repo = repo;
        this.mapper = mapper;
        this.userContext = userContext;
    }

    @Override
    @Transactional
    public ExerciseSetResponse createExerciseSet(CreateExerciseSetRequest exerciseSetRequest) {
        if (exerciseSetRequest == null) {
            throw new IllegalArgumentException("CreateExerciseSetRequest cannot be null");
        }

        // Note: User access validation for create operations should be handled
        // at the controller level since exercise sets are typically created as part of workout progression

        DayExercise dayExercise = entityManager.getReference(DayExercise.class, exerciseSetRequest.dayExerciseId());

        ExerciseSet exerciseSet = new ExerciseSet();
        exerciseSet.setDayExercise(dayExercise);
        exerciseSet.setPosition(exerciseSetRequest.position());
        exerciseSet.setSetType(EnumConverter.stringToEnum(SetType.class, exerciseSetRequest.setType()));
        exerciseSet.setWeightTarget(exerciseSetRequest.weightTarget());
        exerciseSet.setWeightTargetMin(exerciseSetRequest.weightTargetMin());
        exerciseSet.setWeightTargetMax(exerciseSetRequest.weightTargetMax());
        exerciseSet.setRepsTarget(exerciseSetRequest.repsTarget());
        exerciseSet.setCreatedAt(exerciseSetRequest.createdAt() != null ? exerciseSetRequest.createdAt() : Instant.now());

        // Save entity and convert back to payload
        return mapper.toPayload(repo.save(exerciseSet));
    }

    @Override
    @Transactional
    public ExerciseSetResponse updateExerciseSet(Long id, UpdateExerciseSetRequest exerciseSetRequest) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (exerciseSetRequest == null) {
            throw new IllegalArgumentException("UpdateExerciseSetRequest cannot be null");
        }

        Optional<ExerciseSet> exerciseSetOptional = repo.findById(id);
        if (exerciseSetOptional.isEmpty()) {
            throw new RuntimeException("ExerciseSet not found with id: " + id);
        }

        ExerciseSet existing = exerciseSetOptional.get();

        // Validate that the current user owns the mesocycle this exercise set belongs to
        userContext.validateUserAccess(existing.getDayExercise().getDay().getMesocycle().getUserId());

        if (exerciseSetRequest.weight() != null) {
            existing.setWeight(exerciseSetRequest.weight());
        }
        if (exerciseSetRequest.reps() != null) {
            existing.setReps(exerciseSetRequest.reps());
        }

        // Save updated entity and convert back to payload
        return mapper.toPayload(repo.save(existing));
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
