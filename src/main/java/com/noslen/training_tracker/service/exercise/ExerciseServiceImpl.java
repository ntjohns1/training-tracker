package com.noslen.training_tracker.service.exercise;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noslen.training_tracker.dto.exercise.ExercisePayload;
import com.noslen.training_tracker.dto.exercise.ExerciseNotePayload;
import com.noslen.training_tracker.mapper.exercise.ExerciseMapper;
import com.noslen.training_tracker.mapper.exercise.ExerciseNoteMapper;
import com.noslen.training_tracker.model.exercise.Exercise;
import com.noslen.training_tracker.model.exercise.ExerciseNote;
import com.noslen.training_tracker.repository.exercise.ExerciseRepo;

@Service
public class ExerciseServiceImpl implements ExerciseService {
    private final ExerciseRepo repo;
    private final ExerciseMapper mapper;
    private final ExerciseNoteMapper exerciseNoteMapper;

    public ExerciseServiceImpl(ExerciseRepo exerciseRepo, ExerciseMapper exerciseMapper, ExerciseNoteMapper exerciseNoteMapper) {
        this.repo = exerciseRepo;
        this.mapper = exerciseMapper;
        this.exerciseNoteMapper = exerciseNoteMapper;
    }

    @Override
    @Transactional
    public ExercisePayload createExercise(ExercisePayload exercisePayload) {
        if (exercisePayload == null) {
            throw new IllegalArgumentException("ExercisePayload cannot be null");
        }

        // Convert payload to entity
        Exercise exercise = mapper.toEntity(exercisePayload);
        
        // Set creation timestamp if not already set
        if (exercise.getCreatedAt() == null) {
            exercise.setCreatedAt(Instant.now());
        }
        if (exercise.getUpdatedAt() == null) {
            exercise.setUpdatedAt(Instant.now());
        }

        // Save entity
        Exercise savedEntity = repo.save(exercise);

        // Convert back to payload and return
        return mapper.toPayload(savedEntity);
    }

    @Override
    @Transactional
    public ExercisePayload updateExercise(Long exerciseId, ExercisePayload exercisePayload) {
        if (exerciseId == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
        if (exercisePayload == null) {
            throw new IllegalArgumentException("ExercisePayload cannot be null");
        }

        Optional<Exercise> existingOptional = repo.findById(exerciseId);
        if (existingOptional.isEmpty()) {
            throw new RuntimeException("Exercise not found with id: " + exerciseId);
        }

        Exercise existing = existingOptional.get();
        
        // Update entity with payload data using mapper
        mapper.updateEntity(existing, exercisePayload);
        
        // Ensure updated timestamp is set
        existing.setUpdatedAt(Instant.now());
        
        // Save updated entity
        Exercise savedEntity = repo.save(existing);

        // Convert back to payload and return
        return mapper.toPayload(savedEntity);
    }

    @Override
    @Transactional
    public void deleteExercise(Long exerciseId) {
        if (exerciseId == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<Exercise> exerciseToDelete = repo.findById(exerciseId);
        if (exerciseToDelete.isPresent()) {
            exerciseToDelete.get().setDeletedAt(Instant.now());
            repo.save(exerciseToDelete.get()); // Save the deletedAt timestamp
            repo.deleteById(exerciseId);
        } else {
            throw new RuntimeException("Exercise not found with id: " + exerciseId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ExercisePayload getExercise(Long exerciseId) {
        if (exerciseId == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        Optional<Exercise> exercise = repo.findById(exerciseId);
        if (exercise.isEmpty()) {
            throw new RuntimeException("Exercise not found with id: " + exerciseId);
        }
        return mapper.toPayload(exercise.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExercisePayload> getAllExercises() {
        List<Exercise> exercises = repo.findAll();
        return mapper.toPayloadList(exercises);
    }

    @Override
    @Transactional
    public void addExerciseNote(Long exerciseId, ExerciseNotePayload exerciseNotePayload) {
        if (exerciseId == null) {
            throw new IllegalArgumentException("Exercise ID cannot be null");
        }
        if (exerciseNotePayload == null) {
            throw new IllegalArgumentException("ExerciseNotePayload cannot be null");
        }

        Optional<Exercise> exerciseOptional = repo.findById(exerciseId);
        if (exerciseOptional.isEmpty()) {
            throw new RuntimeException("Exercise not found with id: " + exerciseId);
        }

        Exercise exercise = exerciseOptional.get();
        if (exercise.getNotes() == null) {
            exercise.setNotes(new ArrayList<>());
        }
        
        // Convert payload to entity and add to exercise
        ExerciseNote exerciseNote = exerciseNoteMapper.toEntity(exerciseNotePayload);
        exercise.getNotes().add(exerciseNote);
        
        // Update exercise timestamp and save
        exercise.setUpdatedAt(Instant.now());
        repo.save(exercise);
    }

}
