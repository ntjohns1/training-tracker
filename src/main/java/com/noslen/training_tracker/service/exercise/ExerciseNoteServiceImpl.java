package com.noslen.training_tracker.service.exercise;

import com.noslen.training_tracker.dto.exercise.request.CreateExerciseNoteRequest;
import com.noslen.training_tracker.dto.exercise.request.UpdateExerciseNoteRequest;
import com.noslen.training_tracker.dto.exercise.response.ExerciseNoteResponse;
import com.noslen.training_tracker.mapper.exercise.ExerciseNoteMapper;
import com.noslen.training_tracker.model.day.DayExercise;
import com.noslen.training_tracker.model.exercise.Exercise;
import com.noslen.training_tracker.model.exercise.ExerciseNote;
import com.noslen.training_tracker.repository.exercise.ExerciseNoteRepo;
import com.noslen.training_tracker.security.UserContext;
import jakarta.persistence.EntityManager;
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

    private final EntityManager entityManager;
    private final ExerciseNoteRepo repo;
    private final ExerciseNoteMapper mapper;
    private final UserContext userContext;

    public ExerciseNoteServiceImpl(EntityManager entityManager, ExerciseNoteRepo repo, ExerciseNoteMapper mapper, UserContext userContext) {
        this.entityManager = entityManager;
        this.repo = repo;
        this.mapper = mapper;
        this.userContext = userContext;
    }

    @Override
    @Transactional
    public ExerciseNoteResponse createExerciseNote(CreateExerciseNoteRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("CreateExerciseNoteRequest cannot be null");
        }

        // Note: user access for create is handled at the controller level; exercise notes are
        // created as part of workout operations.
        Instant now = Instant.now();
        ExerciseNote exerciseNote = new ExerciseNote();
        exerciseNote.setExercise(request.exerciseId() != null
                ? entityManager.getReference(Exercise.class, request.exerciseId())
                : null);
        exerciseNote.setDayExercise(request.dayExerciseId() != null
                ? entityManager.getReference(DayExercise.class, request.dayExerciseId())
                : null);
        exerciseNote.setNoteId(request.noteId());
        exerciseNote.setText(request.text());
        exerciseNote.setCreatedAt(now);
        exerciseNote.setUpdatedAt(now);

        return mapper.toPayload(repo.save(exerciseNote));
    }

    @Override
    @Transactional
    public ExerciseNoteResponse updateExerciseNote(Long exerciseNoteId, UpdateExerciseNoteRequest request) {
        if (exerciseNoteId == null) {
            throw new IllegalArgumentException("Exercise Note ID cannot be null");
        }
        if (request == null) {
            throw new IllegalArgumentException("UpdateExerciseNoteRequest cannot be null");
        }

        ExerciseNote existing = repo.findById(exerciseNoteId)
                .orElseThrow(() -> new RuntimeException("ExerciseNote not found with id: " + exerciseNoteId));

        // Validate that the current user owns the mesocycle this exercise note belongs to
        userContext.validateUserAccess(existing.getDayExercise().getDay().getMesocycle().getUserId());

        if (request.text() != null) {
            existing.setText(request.text());
        }
        existing.setUpdatedAt(Instant.now());

        return mapper.toPayload(repo.save(existing));
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
