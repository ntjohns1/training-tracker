package com.noslen.training_tracker.service.exercise;

import java.time.Instant;
import java.util.Optional;

import com.noslen.training_tracker.dto.exercise.response.ExerciseNoteResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noslen.training_tracker.mapper.exercise.ExerciseNoteMapper;
import com.noslen.training_tracker.model.exercise.ExerciseNote;
import com.noslen.training_tracker.repository.exercise.ExerciseNoteRepo;

@Service
public class ExerciseNoteServiceImpl implements ExerciseNoteService {
    private final ExerciseNoteRepo repo;
    private final ExerciseNoteMapper mapper;

    public ExerciseNoteServiceImpl(ExerciseNoteRepo exerciseNoteRepo, ExerciseNoteMapper exerciseNoteMapper) {
        this.repo = exerciseNoteRepo;
        this.mapper = exerciseNoteMapper;
    }

    @Override
    @Transactional
    public ExerciseNoteResponse createExerciseNote(ExerciseNoteResponse exerciseNoteResponse) {
        if (exerciseNoteResponse == null) {
            throw new IllegalArgumentException("ExerciseNoteResponse cannot be null");
        }

        // Convert payload to entity
        ExerciseNote exerciseNote = mapper.toEntity(exerciseNoteResponse);
        
        // Set creation timestamp if not already set
        if (exerciseNote.getCreatedAt() == null) {
            exerciseNote.setCreatedAt(Instant.now());
        }
        if (exerciseNote.getUpdatedAt() == null) {
            exerciseNote.setUpdatedAt(Instant.now());
        }

        // Save entity
        ExerciseNote savedEntity = repo.save(exerciseNote);

        // Convert back to payload and return
        return mapper.toPayload(savedEntity);
    }

    @Override
    @Transactional
    public ExerciseNoteResponse updateExerciseNote(Long exerciseNoteId, ExerciseNoteResponse exerciseNoteResponse) {
        if (exerciseNoteId == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (exerciseNoteResponse == null) {
            throw new IllegalArgumentException("ExerciseNoteResponse cannot be null");
        }

        Optional<ExerciseNote> existingOptional = repo.findById(exerciseNoteId);
        if (existingOptional.isEmpty()) {
            throw new RuntimeException("ExerciseNote not found with id: " + exerciseNoteId);
        }

        ExerciseNote existing = existingOptional.get();
        
        // Update entity with payload data using mapper
        mapper.updateEntity(existing,
                            exerciseNoteResponse);
        
        // Ensure updated timestamp is set
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

        if (!repo.existsById(exerciseNoteId)) {
            throw new RuntimeException("ExerciseNote not found with id: " + exerciseNoteId);
        }

        repo.deleteById(exerciseNoteId);
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciseNoteResponse getExerciseNote(Long exerciseNoteId) {
        if (exerciseNoteId == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<ExerciseNote> exerciseNote = repo.findById(exerciseNoteId);
        if (exerciseNote.isEmpty()) {
            throw new RuntimeException("ExerciseNote not found with id: " + exerciseNoteId);
        }
        return mapper.toPayload(exerciseNote.get());
    }
}
