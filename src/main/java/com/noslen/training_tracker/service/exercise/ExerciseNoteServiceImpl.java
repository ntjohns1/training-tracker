package com.noslen.training_tracker.service.exercise;

import com.noslen.training_tracker.dto.exercise.response.ExerciseNoteResponse;
import com.noslen.training_tracker.mapper.exercise.ExerciseNoteMapper;
import com.noslen.training_tracker.model.exercise.ExerciseNote;
import com.noslen.training_tracker.repository.exercise.ExerciseNoteRepo;
import com.noslen.training_tracker.security.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for ExerciseNote operations.
 * Includes user data segregation through UserContext validation.
 */
@Service
public class ExerciseNoteServiceImpl implements ExerciseNoteService {

    private final ExerciseNoteRepo repo;
    private final ExerciseNoteMapper mapper;
    private final UserContext userContext;

    public ExerciseNoteServiceImpl(ExerciseNoteRepo repo, ExerciseNoteMapper mapper, UserContext userContext) {
        this.repo = repo;
        this.mapper = mapper;
        this.userContext = userContext;
    }

    @Override
    @Transactional
    public ExerciseNoteResponse createExerciseNote(ExerciseNoteResponse exerciseNoteResponse) {
        if (exerciseNoteResponse == null) {
            throw new IllegalArgumentException("ExerciseNoteResponse cannot be null");
        }

        // Convert payload to entity
        ExerciseNote exerciseNote = mapper.toEntity(exerciseNoteResponse);
        
        // Set timestamps if not already set
        if (exerciseNote.getCreatedAt() == null) {
            exerciseNote.setCreatedAt(Instant.now());
        }
        if (exerciseNote.getUpdatedAt() == null) {
            exerciseNote.setUpdatedAt(Instant.now());
        }

        // Note: User access validation for create operations should be handled 
        // at the controller level since exercise notes are typically created as part of workout operations

        // Save entity
        ExerciseNote savedEntity = repo.save(exerciseNote);

        // Convert back to payload and return
        return mapper.toPayload(savedEntity);
    }

    @Override
    @Transactional
    public ExerciseNoteResponse updateExerciseNote(Long exerciseNoteId, ExerciseNoteResponse exerciseNoteResponse) {
        if (exerciseNoteId == null) {
            throw new IllegalArgumentException("Exercise Note ID cannot be null");
        }
        if (exerciseNoteResponse == null) {
            throw new IllegalArgumentException("ExerciseNoteResponse cannot be null");
        }

        Optional<ExerciseNote> existingOptional = repo.findById(exerciseNoteId);
        if (existingOptional.isEmpty()) {
            throw new RuntimeException("ExerciseNote not found with id: " + exerciseNoteId);
        }

        ExerciseNote existing = existingOptional.get();
        
        // Validate that the current user owns the mesocycle this exercise note belongs to
        userContext.validateUserAccess(existing.getDayExercise().getDay().getMesocycle().getUserId());

        // Update entity with payload data using mapper
        mapper.updateEntity(existing, exerciseNoteResponse);
        
        // Update timestamp
        existing.setUpdatedAt(Instant.now());
        
        // Save updated entity
        ExerciseNote savedEntity = repo.save(existing);

        // Convert back to payload and return
        return mapper.toPayload(savedEntity);
    }

    @Override
    @Transactional
    public void deleteExerciseNote(Long exerciseNoteId) {
        if (exerciseNoteId == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<ExerciseNote> existingOptional = repo.findById(exerciseNoteId);
        if (existingOptional.isEmpty()) {
            throw new RuntimeException("ExerciseNote not found with id: " + exerciseNoteId);
        }

        ExerciseNote existing = existingOptional.get();
        
        // Validate that the current user owns the mesocycle this exercise note belongs to
        userContext.validateUserAccess(existing.getDayExercise().getDay().getMesocycle().getUserId());

        repo.deleteById(exerciseNoteId);
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciseNoteResponse getExerciseNote(Long exerciseNoteId) {
        if (exerciseNoteId == null) {
            throw new IllegalArgumentException("Exercise Note ID cannot be null");
        }

        Optional<ExerciseNote> exerciseNote = repo.findById(exerciseNoteId);
        if (exerciseNote.isEmpty()) {
            throw new RuntimeException("ExerciseNote not found with id: " + exerciseNoteId);
        }
        
        // Validate that the current user owns the mesocycle this exercise note belongs to
        userContext.validateUserAccess(exerciseNote.get().getDayExercise().getDay().getMesocycle().getUserId());

        return mapper.toPayload(exerciseNote.get());
    }
}
