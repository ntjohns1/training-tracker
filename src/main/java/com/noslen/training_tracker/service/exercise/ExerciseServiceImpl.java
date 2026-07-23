package com.noslen.training_tracker.service.exercise;

import java.util.List;
import java.util.Optional;

import com.noslen.training_tracker.dto.exercise.response.ExerciseResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.noslen.training_tracker.mapper.exercise.ExerciseMapper;
import com.noslen.training_tracker.model.exercise.Exercise;
import com.noslen.training_tracker.repository.exercise.ExerciseRepo;

/**
 * Read access to the exercise catalog. The catalog is seeded reference data; custom-exercise
 * writes are not part of the MVP.
 */
@Service
public class ExerciseServiceImpl implements ExerciseService {
    private final ExerciseRepo repo;
    private final ExerciseMapper mapper;

    public ExerciseServiceImpl(ExerciseRepo exerciseRepo, ExerciseMapper exerciseMapper) {
        this.repo = exerciseRepo;
        this.mapper = exerciseMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ExerciseResponse getExercise(Long exerciseId) {
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
    public List<ExerciseResponse> getAllExercises() {
        List<Exercise> exercises = repo.findAll();
        return mapper.toPayloadList(exercises);
    }
}
