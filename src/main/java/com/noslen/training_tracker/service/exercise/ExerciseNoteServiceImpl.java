package com.noslen.training_tracker.service.exercise;

import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noslen.training_tracker.dto.exercise.ExerciseNotePayload;
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
    public ExerciseNotePayload createExerciseNote(ExerciseNotePayload exerciseNotePayload) {
        if (exerciseNotePayload == null) {
            throw new IllegalArgumentException("ExerciseNotePayload cannot be null");
        }

        // Convert payload to entity
        ExerciseNote exerciseNote = mapper.toEntity(exerciseNotePayload);
        
        // Set creation timestamp if not already set
        if (exerciseNote.getCreatedAt() == null) {
            exerciseNote = ExerciseNote.builder()
                    .id(exerciseNote.getId())
                    .userId(exerciseNote.getUserId())
                    .noteId(exerciseNote.getNoteId())
                    .exercise(exerciseNote.getExercise())
                    .dayExercise(exerciseNote.getDayExercise())
                    .createdAt(Instant.now())
                    .updatedAt(exerciseNote.getUpdatedAt() != null ? exerciseNote.getUpdatedAt() : Instant.now())
                    .text(exerciseNote.getText())
                    .build();
        }
        if (exerciseNote.getUpdatedAt() == null) {
            exerciseNote = ExerciseNote.builder()
                    .id(exerciseNote.getId())
                    .userId(exerciseNote.getUserId())
                    .noteId(exerciseNote.getNoteId())
                    .exercise(exerciseNote.getExercise())
                    .dayExercise(exerciseNote.getDayExercise())
                    .createdAt(exerciseNote.getCreatedAt())
                    .updatedAt(Instant.now())
                    .text(exerciseNote.getText())
                    .build();
        }

        // Save entity
        ExerciseNote savedEntity = repo.save(exerciseNote);

        // Convert back to payload and return
        return mapper.toPayload(savedEntity);
    }

    @Override
    @Transactional
    public ExerciseNotePayload updateExerciseNote(Long exerciseNoteId, ExerciseNotePayload exerciseNotePayload) {
        if (exerciseNoteId == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (exerciseNotePayload == null) {
            throw new IllegalArgumentException("ExerciseNotePayload cannot be null");
        }

        Optional<ExerciseNote> existingOptional = repo.findById(exerciseNoteId);
        if (existingOptional.isEmpty()) {
            throw new RuntimeException("ExerciseNote not found with id: " + exerciseNoteId);
        }

        ExerciseNote existing = existingOptional.get();
        
        // Update entity with payload data using mapper
        mapper.updateEntity(existing, exerciseNotePayload);
        
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
    public ExerciseNotePayload getExerciseNote(Long exerciseNoteId) {
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
