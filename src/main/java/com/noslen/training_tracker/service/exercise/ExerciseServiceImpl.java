package com.noslen.training_tracker.service.exercise;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.noslen.training_tracker.model.exercise.Exercise;
import com.noslen.training_tracker.model.exercise.ExerciseNote;
import com.noslen.training_tracker.repository.exercise.ExerciseRepo;

@Service
public class ExerciseServiceImpl implements ExerciseService {
    private final ExerciseRepo repo;

    public ExerciseServiceImpl(ExerciseRepo exerciseRepo) {
        this.repo = exerciseRepo;
    }

    @Override
    public Exercise createExercise(Exercise exercise) {
        exercise.setCreatedAt(Instant.now());
        exercise.setUpdatedAt(Instant.now());
        return this.repo.save(exercise);
    }

    @Override
    public Exercise updateExercise(Long exerciseId, Exercise exercise) {
        Optional<Exercise> exerciseToUpdate = this.repo.findById(exerciseId);
        if (exerciseToUpdate.isPresent()) {
            exerciseToUpdate.get().setName(exercise.getName());
            exerciseToUpdate.get().setMuscleGroupId(exercise.getMuscleGroupId());
            exerciseToUpdate.get().setExerciseType(exercise.getExerciseType());
            exerciseToUpdate.get().setYoutubeId(exercise.getYoutubeId());
            exerciseToUpdate.get().setMgSubType(exercise.getMgSubType());
            exerciseToUpdate.get().setUpdatedAt(Instant.now());
            this.repo.save(exerciseToUpdate.get());
        }
        return exerciseToUpdate.orElse(null);
    }

    @Override
    public void deleteExercise(Long exerciseId) {
        Optional<Exercise> exerciseToDelete = this.repo.findById(exerciseId);
        if (exerciseToDelete.isPresent()) {
            exerciseToDelete.get().setDeletedAt(Instant.now());
            this.repo.deleteById(exerciseId);
        }
    }

    @Override
    public Exercise getExercise(Long exerciseId) {
        Optional<Exercise> exercise = this.repo.findById(exerciseId);
        return exercise.orElse(null);
    }

    @Override
    public List<Exercise> getAllExercises() {
        return this.repo.findAll();
    }

    @Override
    public void addExerciseNote(Long exerciseId, ExerciseNote exerciseNote) {   
        Optional<Exercise> exercise = this.repo.findById(exerciseId);
        if (exercise.isPresent()) {
            Exercise exerciseEntity = exercise.get();
            if (exerciseEntity.getNotes() == null) {
                exerciseEntity.setNotes(new ArrayList<>());
            }
            exerciseEntity.getNotes().add(exerciseNote);
            this.repo.save(exerciseEntity);
        }
    }   

}
